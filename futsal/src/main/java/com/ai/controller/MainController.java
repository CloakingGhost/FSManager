package com.ai.controller;

import com.ai.domain.*;
import com.ai.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Controller
@Api(tags = {"메인컨트롤러 API"})
public class MainController {
    private String clientId;
    private String redirectURI;
    KaKaoAPI kakaoApi = new KaKaoAPI();
    NaverAPI naverApi = new NaverAPI();
    GoogleAPI googleApi = new GoogleAPI();

    @Value("${kakao.oauth.login.clientId}")
    String kakao_clientId;

    @Value("${google.oauth.login.clientId}")
    String google_clientId;
    @Value("${google.oauth.login.clientSecret}")
    String google_clientSecret;

    @Value("${naver.oauth.login.dev.clientId}")
    String naver_dev_clientId;
    @Value("${naver.oauth.login.dev.clientSecret}")
    String naver_dev_clientSecret;

    @Value("${naver.oauth.login.prod.clientId}")
    String naver_prod_clientId;
    @Value("${naver.oauth.login.prod.clientSecret}")
    String naver_prod_clientSecret;

    @Autowired
    FieldService fService;
    @Autowired
    MemberService mService;
    @Autowired
    TeamService tService;
    @Autowired
    ReserveService rService;
    @Autowired
    ReserveListService rlService;
    @Autowired
    ReservationService rvService;
    @Autowired
    FieldReviewService frService;
    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    private static double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515 * 1609.344;

        return dist; //단위 meter
    }

    //10진수를 radian(라디안)으로 변환
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    //radian(라디안)을 10진수로 변환
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    @RequestMapping(value = "/mainSortLatitudeLongitudeDistanceNearBidongki", method = RequestMethod.POST)
    @ApiOperation(value = "근처 거리 순으로 구장 비동기로 정렬", response = FieldDTO.class)
    @ResponseBody
    public ArrayList<FieldDTO> postmainSortLatitudeLongitudeDistanceNearAjax(@RequestParam("lat") String lat, @RequestParam("lon") String lon) {
        log.info("mainSortLatitudeLongitudeDistanceNearBidongki 컨트롤러 들어옴,,,, lat : " + lat + " lon : " + lon);
        ArrayList<FieldDTO> fieldList = fService.findAll();
        ArrayList<Distance> disList = new ArrayList<>();
        ArrayList<FieldDTO> fList = new ArrayList<>();
        // 각 구장별 거리
        for (int i = 0; i < fieldList.size(); i++) {
            double dist = distance(Double.parseDouble(lat), Double.parseDouble(lon),
                    Double.parseDouble(fieldList.get(i).getLatitude()), Double.parseDouble(fieldList.get(i).getLongitude()));
            Distance dis = new Distance();
            dis.setField(fieldList.get(i).getFName());
            dis.setDistance(dist);
            disList.add(dis);
        }
        Comparator<Distance> comparator = new Comparator<Distance>() {
            public int compare(Distance a, Distance b) {
                return Double.compare(a.getDistance(), b.getDistance());
            }
        };
        Collections.sort(disList, comparator);
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < fieldList.size(); j++) {
                if (disList.get(i).getField().equals(fieldList.get(j).getFName())) {
                    fList.add(fieldList.get(j));
                }
            }
        }
        return fList;
    }

    @RequestMapping(value = "/header")
    public void getheader() {
    }


    @RequestMapping(value = "/createTeam")
    @ApiOperation(value = "팀 생성으로 이동", response = TeamDTO.class)
    public ModelAndView getCreateTeam(HttpSession session) {
        ModelAndView mav = new ModelAndView();
        MemberDTO member = mService.findByid((String) session.getAttribute("userId"));
        Integer point = null;
        try {
            point = member.getHadPoint();
        } catch (Exception e) {
            log.info(e.getMessage());
        }

        Utils.getAlarms(mav, Utils.userInfo);
        String uuid = UUID.randomUUID().toString();
        mav.addObject("member", Utils.userInfo);
        mav.addObject("point", point);
        mav.addObject("uuid", uuid);
        return mav;
    }

    @RequestMapping(value = "/awayList", method = RequestMethod.POST)
    @ResponseBody
    public ArrayList<ReserveListDTO> awayList(@RequestParam("date") String date, @RequestParam("field") String field,
                                              @RequestParam("time") String time) {
        ArrayList<ReserveListDTO> awayList = new ArrayList<>();
        TeamDTO team;
        awayList.addAll(rlService.findByFieldAndDateAndTime(field, date, time));
        try {
            int size = awayList.size();//연령대 총 판수 매너점수 로고 유니폼색 추가해야함
            for (int i = 0; i < size; i++) {
                team = tService.findBytName(awayList.get(i).getTeam());
                awayList.get(i).setUniform(team.getUniform());
                awayList.get(i).setLogoPath(team.getLogoPath());
                awayList.get(i).setTTotal(team.getTTotal());
                awayList.get(i).setTManner(team.getTManner());
                awayList.get(i).setTAge(team.getTAge());
            }
        } catch (Exception e) {
            log.info("error message : " + e.getMessage());
            return awayList;
        }

        return awayList;
    }

    @ResponseBody
    @RequestMapping(value = "/reservation")
    @ApiOperation(value = "예약목록페이지로 이동", response = ReservationDTO.class)
    public ModelAndView getReservation(HttpSession session, @PageableDefault(size = 10) Pageable pageable) throws Exception {
        ModelAndView mav = new ModelAndView();
        ZonedDateTime nowSeoul = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        String parsedcurDateTime = nowSeoul.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); //현재시간을 String으로 파씽
        String nowDate = nowSeoul.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String nowDate2 = nowDate.replace("-", "/");
        Date format1 = new SimpleDateFormat("yyyy/MM/dd").parse(nowDate2);
        int hours = nowSeoul.getHour();

        TeamDTO team = new TeamDTO();
        MemberDTO member = mService.findByid((String) session.getAttribute("userId"));
        String nickName = member.getNickName();
        Integer point = null;
        List<Object> dataPackage = new ArrayList<>();
        LinkedHashMap<ReservationVO, Integer> dataPackagebf = new LinkedHashMap<>(); // 경기전
        LinkedHashMap<ReservationVO, Integer> dataPackageaf = new LinkedHashMap<>(); // 경기후
        ArrayList<ReservationVO> reservationList = new ArrayList<>();
        Integer fieldPrice;
        try {
            reservationList = rvService.findByid(nickName).getInfo();
            for (int i = reservationList.size() - 1; i >= 0; i--) {
                ReservationVO vo = reservationList.get(i);
                String[] timeArray = vo.getTime().split("시 - ");
                String[] timeArray2 = timeArray[1].split("시");
                String voDate = vo.getDate().replace("-", "/");
                Date format2 = new SimpleDateFormat("yyyy/MM/dd").parse(voDate);
                fieldPrice = Integer.parseInt(reservationList.get(i).getPrice().replace(",", "").replace(" P", ""));
                try {
                    vo.getMannerornot();
                } catch (Exception e) {
                    vo.setMannerornot(null);
                }

                ArrayList<Object> temp = new ArrayList<>();
                temp.add(vo);
                temp.add(fieldPrice);
                dataPackage.add(temp);
                if ((vo.getDate().equals(nowDate) && hours >= Integer.parseInt(timeArray2[0])) || (format1.getTime() - format2.getTime()) / (24 * 60 * 60) > 0) {
                    dataPackageaf.put(vo, fieldPrice); // 경기후
                } else {
                    dataPackagebf.put(vo, fieldPrice); // 경기전
                }
            }

        } catch (Exception e) {
            reservationList.add(new ReservationVO());
        }

        try {
            point = member.getHadPoint();
            team = tService.findBytName(member.getTName());
        } catch (Exception e) {
            log.info(e.getMessage());
            team = null;
        }
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), dataPackage.size());
        List<Object> subList = start >= end ? new ArrayList<>() : dataPackage.subList(start, end);
        Page<Object> pDto = new PageImpl<>(subList, pageable, dataPackage.size());


        Utils.getAlarms(mav, member);
        ArrayList<FieldDTO> fList = new ArrayList<>();
        fList = fService.findAll();
        Utils.fNList = new ArrayList<String>();
        Utils.latList = new ArrayList<String>();
        Utils.lonList = new ArrayList<String>();
        for (int i = 0; i < fList.size(); i++) {
            Utils.fNList.add(fList.get(i).getFName());
            Utils.latList.add(fList.get(i).getLatitude());
            Utils.lonList.add(fList.get(i).getLongitude());
        }
        mav.addObject("fNList", Utils.fNList);
        mav.addObject("latList", Utils.latList);
        mav.addObject("lonList", Utils.lonList);
        mav.addObject("uuid", UUID.randomUUID().toString());
        mav.addObject("member", member);
        mav.addObject("team", team);
        mav.addObject("point", point);
        mav.addObject("dataPackagebf", dataPackagebf);
        mav.addObject("dataPackageaf", dataPackageaf);
        mav.addObject("pDto", pDto);

        return mav;
    }

    @RequestMapping(value = "/teamtables", method = RequestMethod.GET)
    @ApiOperation(value = "팀 메인페이지으로 이동", response = TeamDTO.class)
    public ModelAndView getTeamTables(HttpSession session) {
        // TEST CODE
        TeamDTO team = new TeamDTO();
        ModelAndView mav = new ModelAndView();
        /*log.info("=============== Sesssion ==============");
        log.info("세션 유저 아이디 : {}",session.getAttribute("userId"));
        log.info("=============== Sesssion ==============");
        log.info("=============== RESERVE MAV ===================");
        log.info("member : {}", mService.findByid((String) session.getAttribute("userId")));*/
        // END TEST
        try {
            team = tService.findBytName(mService.findByid((String) session.getAttribute("userId")).getTName());
        } catch (Exception e) {
            log.info(mService.findByid((String) session.getAttribute("userId")).getNickName() + "회원님은 팀이 없습니다 ");
            team = null;
        }

        String _id = (String) (session.getAttribute("userId"));
        MemberDTO member = new MemberDTO();
        member = mService.findByid(_id);
        MemberDTO dto = mService.findByid((String) session.getAttribute("userId"));
        Integer point = null;
        try {
            point = dto.getHadPoint();
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        ArrayList<AlarmVO> readalarms = new ArrayList<AlarmVO>();
        ArrayList<AlarmVO> unreadalarms = new ArrayList<AlarmVO>();
        try {
            for (int i = member.getAlarms().size() - 1; i >= 0; i--) {
                if (member.getAlarms().get(i).getIsRead().equals("읽음")) {
                    readalarms.add(member.getAlarms().get(i));
                }
            }
        } catch (Exception e) {
            log.info(e.getMessage());
            readalarms = null;
        }
        try {
            for (int i = member.getAlarms().size() - 1; i >= 0; i--) {
                if (member.getAlarms().get(i).getIsRead().equals("안읽음")) {
                    unreadalarms.add(member.getAlarms().get(i));
                }
            }
        } catch (Exception e) {
            log.info(e.getMessage());
            unreadalarms = null;
        }
        ArrayList<TeamDTO> tList = tService.findAll();
        String uuid = UUID.randomUUID().toString();
        mav.addObject("uuid", uuid);
        mav.addObject("fNList", Utils.fNList);
        mav.addObject("latList", Utils.latList);
        mav.addObject("lonList", Utils.lonList);
        mav.addObject("unreadalarms", unreadalarms);
        mav.addObject("readalarms", readalarms);
        mav.addObject("point", point);
        mav.addObject("member", member);
        mav.addObject("team", team);
        mav.addObject("tList", tList);
        return mav;
    }

    @RequestMapping(value = "/register")
    public ModelAndView getRegister(HttpServletRequest request) throws UnsupportedEncodingException {

        // https://developers.naver.com/docs/login/api/api.md 참조
        ModelAndView mav = new ModelAndView();
        if (CheckDevenv.DEVENV == false) {
            // 서버 배포용
            clientId = naver_prod_clientId;
            try {
                redirectURI = URLEncoder.encode("https://www.fsmanager.run" + request.getContextPath() + "/loginAccess", "UTF-8");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            // 개발용
            clientId = naver_dev_clientId;
            try {
                redirectURI = URLEncoder.encode("http://localhost:8080/loginAccess", "UTF-8");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        SecureRandom random = new SecureRandom();
        // state 는 Naver 사에서 'CSRF를 방지하기 위한 인증값입니다. 임의의 값을 넣어 진행해주시면 되는데요.' 라고 답변 (난수
        // 입력)
        String state = new BigInteger(130, random).toString();
        String apiURL = "https://nid.naver.com/oauth2.0/authorize?response_type=code&prompt=login";
        apiURL += "&client_id=" + clientId;
        apiURL += "&redirect_uri=" + redirectURI;
        apiURL += "&state=" + state;
        apiURL += "&prompt=login";
        mav.addObject("apiURL", apiURL);
        mav.setViewName("/register");
        return mav;
    }

    @RequestMapping(value = "/loginAccess")
    @ApiOperation(value = "로그인 접근", response = MemberDTO.class)
    public ModelAndView login(@RequestParam("code") String code, HttpSession session, HttpServletRequest request)
            throws UnsupportedEncodingException {
        ModelAndView mav = new ModelAndView();
        System.out.println("code : " + code);
        String access_token = null;
        String state = null;
        String platform = null;
        String name = null;
        String nickName = null;
        String sex = null;
        String phoneNo = null;
        //String tName = "null";
        int hadPoint = 1000000;
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String now = sdf.format(date);
        MemberDTO member = new MemberDTO();

        HashMap<String, Object> userInfo = null;
        Cookie[] cookieArr = request.getCookies();
        for (Cookie cookie : cookieArr) {
            System.out.println("cookie : " + cookie);
            if (cookie.getName().equals("platform")) {
                platform = cookie.getValue();
                System.out.println("platform : " + platform);
            }
            // REGISTER
            if (cookie.getName().equals("name")) {
                name = cookie.getValue();
                System.out.println("Cookie name : " + name);
            }
            if (cookie.getName().equals("nickName")) {
                nickName = cookie.getValue();
                System.out.println("Cookie nickName : " + nickName);
            }
            if (cookie.getName().equals("sex")) {
                sex = cookie.getValue();
                System.out.println("Cookie sex : " + sex);
            }
            if (cookie.getName().equals("phoneNo")) {
                phoneNo = cookie.getValue();
                System.out.println("Cookie phoneNo : " + phoneNo);
            }
            // REGISTER
        }

        // CHECK PLATFORM
        if (platform.equals("kakao")) {
            access_token = kakaoApi.getAccessToken(code, request, kakao_clientId);
            userInfo = kakaoApi.getUserInfo(access_token);
        }

        // 네이버 일때
        if (platform.equals("naver")) {
            if (CheckDevenv.DEVENV == false) {
                // 서버 배포용
                access_token = naverApi.getAccessToken(code, session, request, naver_prod_clientId, naver_prod_clientSecret);
            } else {
                access_token = naverApi.getAccessToken(code, session, request, naver_dev_clientId, naver_dev_clientSecret);

            }
            state = (String) session.getAttribute("state");
            userInfo = naverApi.getUserInfo(access_token);
        }

        // 구글 일때
        if (platform.equals("google")) {
            access_token = googleApi.getAccessToken(code, request, google_clientId, google_clientSecret);
            userInfo = googleApi.getUserInfo(access_token);
        }
        // 이메일 set
        if (userInfo.get("email") != null) {
            session.setAttribute("userId", userInfo.get("email"));
            session.setAttribute("access_token", access_token);
            Utils.userInfo = mService.findByid((String) userInfo.get("email"));
        }
        // 회원가입 진행 했는지 판단 여부
        if (name != null) {
            member.setId((String) userInfo.get("email"));
            member.setPlatform(platform);
            member.setRegDate(now);
            member.setName(name);
            member.setNickName(nickName);
            member.setSex(sex);
            member.setPhoneNo(phoneNo);
            member.setHadPoint(hadPoint);
            member.setBlockList(new ArrayList<String>());
            try {
                MemberDTO testMember = mService.findByid((String) userInfo.get("email"));
                log.info(testMember.getNickName());
                // 유저 정보 존재하면
                String href = "/logout";
                if (!CheckDevenv.DEVENV) {
                    href = request.getContextPath() + "/logout";
                }
                mav.addObject("data", new Message("이미 존재하는 회원입니다.", href));
                mav.setViewName("/message");
                return mav;
            } catch (Exception e) {
                mService.insert(member);
            }
        }

        // 로그인 했을경우
        log.info("TRY LOGIN");
        log.info("login platform : " + platform);
        log.info("access_token : " + access_token);
        log.info("login info : " + userInfo.toString());
        try {
            MemberDTO testMember = mService.findByid((String) userInfo.get("email"));
            log.info("회원 정보 존재" + testMember.getNickName());
            mav.setViewName("redirect:/main");
        } catch (Exception e) {
            System.out.println("회원정보에 없는 유저가 로그인");
            String href2 = "/logout2";
            if (!CheckDevenv.DEVENV) {
                href2 = request.getContextPath() + "/logout2";
            }
            mav.addObject("data", new Message("등록된 회원 정보가 없습니다. 회원가입 페이지로 이동합니다.", href2));
            mav.setViewName("/message");
        }
        return mav;
    }

    @RequestMapping(value = "/")
    @ApiOperation(value = "루트로 접근 시 메인으로 이동")
    public String goMain() {
        return "redirect:/main";
    }

    @RequestMapping(value = "/main", method = RequestMethod.GET)
    @ApiOperation(value = "메인페이지로 이동")
    public ModelAndView getMain(HttpSession session, Model model, HttpServletRequest request) throws UnsupportedEncodingException {
        ModelAndView mav = new ModelAndView();
        ArrayList<FieldDTO> fList = fService.findAll();
        ArrayList<FieldDTO> fList1 = new ArrayList<FieldDTO>();
        TeamDTO team = new TeamDTO();
        ArrayList<AlarmVO> alarms = new ArrayList<AlarmVO>();


        for (int i = 0; i < 12; i++) {
            fList1.add(fList.get(i));
        }
        mav.addObject("fList1", fList1);

        Utils.fNList = new ArrayList<String>();
        Utils.latList = new ArrayList<String>();
        Utils.lonList = new ArrayList<String>();
        for (int i = 0; i < fList.size(); i++) {
            Utils.fNList.add(fList.get(i).getFName());
            Utils.latList.add(fList.get(i).getLatitude());
            Utils.lonList.add(fList.get(i).getLongitude());
        }
        if (CheckDevenv.DEVENV == false) {
            // 서버 배포용
            clientId = naver_prod_clientId;
            try {
                redirectURI = URLEncoder.encode("https://www.fsmanager.run" + request.getContextPath() + "/loginAccess", "UTF-8");
            } catch (Exception e) {
                System.out.println(e);
            }
        } else {
            // 개발용
            clientId = naver_dev_clientId;
            try {
                redirectURI = URLEncoder.encode("http://localhost:8080/loginAccess", "UTF-8");
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        SecureRandom random = new SecureRandom();
        // state 는 Naver 사에서 'CSRF를 방지하기 위한 인증값입니다. 임의의 값을 넣어 진행해주시면 되는데요.' 라고 답변 (난수
        // 입력)
        String state = new BigInteger(130, random).toString();
        String apiURL = "https://nid.naver.com/oauth2.0/authorize?response_type=code";
        apiURL += "&client_id=" + clientId;
        apiURL += "&redirect_uri=" + redirectURI;
        apiURL += "&state=" + state;
        apiURL += "&prompt=login";


        // 해당 구장에 이미 좋아요를 눌렀으면 true, 반대면 false
        MemberDTO member = mService.findByid((String) session.getAttribute("userId"));
        ArrayList<String> likelist = null;
        Integer point = null;
        boolean likefieldisempty = false;
        ArrayList<AlarmVO> readalarms = new ArrayList<AlarmVO>();
        ArrayList<AlarmVO> unreadalarms = new ArrayList<AlarmVO>();

        try {
            likelist = member.getLikeFieldList();
            team = tService.findBytName(member.getTName());
            likefieldisempty = true;
            point = member.getHadPoint();

        } catch (Exception e) {
            team = null;
            log.info("해당 회원은 팀이없거나, 좋아요를 누른 구장이 없습니다.");
        }

        try {
            for (int i = member.getAlarms().size() - 1; i >= 0; i--) {
                if (member.getAlarms().get(i).getIsRead().equals("읽음")) {
                    readalarms.add(member.getAlarms().get(i));
                }
            }
        } catch (Exception e) {
            log.info(e.getMessage());
            readalarms = null;
        }
        try {
            for (int i = member.getAlarms().size() - 1; i >= 0; i--) {
                if (member.getAlarms().get(i).getIsRead().equals("안읽음")) {
                    unreadalarms.add(member.getAlarms().get(i));
                }
            }
        } catch (Exception e) {
            log.info(e.getMessage());
            unreadalarms = null;
        }
        boolean searchedfield = false;
        String uuid;
        uuid = UUID.randomUUID().toString();

        log.info("fNList[0] : " + Utils.fNList.get(0));
        mav.addObject("uuid", uuid);
        mav.addObject("searchedfield", searchedfield);
        mav.addObject("unreadalarms", unreadalarms);
        mav.addObject("readalarms", readalarms);
        mav.addObject("team", team);
        mav.addObject("point", point);
        mav.addObject("likefieldisempty", likefieldisempty);
        mav.addObject("likelist", likelist);
        mav.addObject("member", member);
        mav.addObject("apiURL", apiURL);
        mav.addObject("fNList", Utils.fNList);
        mav.addObject("latList", Utils.latList);
        mav.addObject("lonList", Utils.lonList);
        mav.addObject("fList", fList);
        mav.setViewName("/main");
        return mav;
    }

    @RequestMapping(value = "/main", method = RequestMethod.POST)
    @ApiOperation(value = "구장명 및 구장주소로 검색 후 메인 페이지로 이동")
    public ModelAndView postMain(@RequestParam("searchField") String fName, HttpSession session, Model model, HttpServletRequest request) throws UnsupportedEncodingException {
        ModelAndView mav = new ModelAndView();
        ArrayList<FieldDTO> dtoList = fService.findAll();
        List<FieldDTO> result = new ArrayList<>();
        for (int i = 0; i < dtoList.size(); i++) {
            if (dtoList.get(i).getFName().contains(fName) || dtoList.get(i).getFAddress().contains(fName)) {
                result.add(dtoList.get(i));
            }
        }
        SearchMainDTO searchMain = new SearchMainDTO();
        searchMain.setNAfields(result);
        mav.addObject("fList1", result);

        ArrayList<FieldDTO> fList = fService.findAll();
        ArrayList<FieldDTO> NfList = fService.findByFNameRegex(fName);
        ArrayList<FieldDTO> AfList = fService.findByFAddressRegex(fName);
//        List<FieldDTO> NAfields = new ArrayList<>(NfList);
//        NAfields.removeAll(AfList);
//        NAfields.addAll(AfList);
        TeamDTO team = new TeamDTO();

//        mav.addObject("fList1", NAfields);
        ArrayList<String> fNList = new ArrayList<String>();
        ArrayList<String> latList = new ArrayList<String>();
        ArrayList<String> lonList = new ArrayList<String>();
        for (int i = 0; i < fList.size(); i++) {
            fNList.add(fList.get(i).getFName());
            latList.add(fList.get(i).getLatitude());
            lonList.add(fList.get(i).getLongitude());
        }
        if (CheckDevenv.DEVENV == false) {
            // 서버 배포용
            clientId = naver_prod_clientId;
            try {
                redirectURI = URLEncoder.encode("https://www.fsmanager.run" + request.getContextPath() + "/loginAccess", "UTF-8");
            } catch (Exception e) {
                System.out.println(e);
            }
        } else {
            // 개발용
            clientId = naver_dev_clientId;
            try {
                redirectURI = URLEncoder.encode("http://localhost:8080/loginAccess", "UTF-8");
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        SecureRandom random = new SecureRandom();
        // state 는 Naver 사에서 'CSRF를 방지하기 위한 인증값입니다. 임의의 값을 넣어 진행해주시면 되는데요.' 라고 답변 (난수
        // 입력)
        String state = new BigInteger(130, random).toString();
        String apiURL = "https://nid.naver.com/oauth2.0/authorize?response_type=code";
        apiURL += "&client_id=" + clientId;
        apiURL += "&redirect_uri=" + redirectURI;
        apiURL += "&state=" + state;
        apiURL += "&prompt=login";
        MemberDTO member = null;
        Integer point = null;
        ArrayList<String> likelist = null;
        boolean likefieldisempty = false;
        try {
            member = mService.findByid((String) session.getAttribute("userId"));
            point = member.getHadPoint();
            likelist = member.getLikeFieldList();
            team = tService.findBytName(member.getTName());
            likefieldisempty = true;
        } catch (Exception e) {
            log.info(e.getMessage());
            member = new MemberDTO();
            team = null;
        }

        boolean searchedfield = true;
        ArrayList<AlarmVO> readalarms = new ArrayList<AlarmVO>();
        ArrayList<AlarmVO> unreadalarms = new ArrayList<AlarmVO>();

        try {
            for (int i = member.getAlarms().size() - 1; i >= 0; i--) {
                if (member.getAlarms().get(i).getIsRead().equals("읽음")) {
                    readalarms.add(member.getAlarms().get(i));
                }
            }
        } catch (Exception e) {
            log.info(e.getMessage());
            readalarms = null;
        }
        try {
            for (int i = member.getAlarms().size() - 1; i >= 0; i--) {
                if (member.getAlarms().get(i).getIsRead().equals("안읽음")) {
                    unreadalarms.add(member.getAlarms().get(i));
                }
            }
        } catch (Exception e) {
            log.info(e.getMessage());
            unreadalarms = null;
        }


        mav.addObject("unreadalarms", unreadalarms);
        mav.addObject("readalarms", readalarms);
        mav.addObject("team", team);
        mav.addObject("likefieldisempty", likefieldisempty);
        mav.addObject("searchedfield", searchedfield);
        mav.addObject("likelist", likelist);
        mav.addObject("member", member);
        mav.addObject("point", point);
        mav.addObject("apiURL", apiURL);
        mav.addObject("fNList", fNList);
        mav.addObject("latList", latList);
        mav.addObject("lonList", lonList);
        mav.addObject("fList", fList);
        mav.setViewName("/main");
        return mav;
    }

    @RequestMapping(value = "/logout")
    @ApiOperation(value = "로그아웃")
    public ModelAndView logout(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView();
        String platform = null;
        Cookie[] cookieArr = request.getCookies();
        for (Cookie cookie : cookieArr) {
            if (cookie.getName().equals("platform")) {
                platform = cookie.getValue();
            }
        }
        try {
            if (platform.equals("kakao")) {
                kakaoApi.logout((String) session.getAttribute("access_token"));
                session.invalidate();
            }
            if (platform.equals("naver")) {
                if (!CheckDevenv.DEVENV) {
                    naverApi.logout((String) session.getAttribute("access_token"), naver_prod_clientId, naver_prod_clientSecret);
                } else {
                    naverApi.logout((String) session.getAttribute("access_token"), naver_dev_clientId, naver_dev_clientSecret);
                }
                session.invalidate();
            }
            if (platform.equals("google")) {
                googleApi.logout((String) session.getAttribute("access_token"));
                session.invalidate();
            }
        } catch (Exception e) {
            session.invalidate();
        }

        for (Cookie cookie : cookieArr) {
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
        mav.setViewName("redirect:/main");
        return mav;
    }

    @RequestMapping(value = "/logout2")
    @ApiOperation(value = "로그인시 존재하지 않는 회원일 때 회원가입페이지로 이동")
    public ModelAndView logout2(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView();
        String platform = null;
        Cookie[] cookieArr = request.getCookies();
        for (Cookie cookie : cookieArr) {
            if (cookie.getName().equals("platform")) {
                platform = cookie.getValue();
            }
        }
        try {
            if (platform.equals("kakao")) {
                kakaoApi.logout((String) session.getAttribute("access_token"));
                session.invalidate();
            }
            if (platform.equals("naver")) {
                if (!CheckDevenv.DEVENV) {
                    naverApi.logout((String) session.getAttribute("access_token"), naver_prod_clientId, naver_prod_clientSecret);
                } else {
                    naverApi.logout((String) session.getAttribute("access_token"), naver_dev_clientId, naver_dev_clientSecret);
                }
                session.invalidate();
            }
            if (platform.equals("google")) {
                googleApi.logout((String) session.getAttribute("access_token"));
                session.invalidate();
            }
        } catch (Exception e) {
            session.invalidate();
        }

        for (Cookie cookie : cookieArr) {
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
        mav.setViewName("redirect:/register");
        return mav;
    }

    @RequestMapping(value = "/reserve/{selectField}/{time}/{type}")
    @ApiOperation(value = "예약페이지로 이동", response = ReserveDTO.class)
    public ModelAndView getReserve(@PathVariable("selectField") String selectField, @PathVariable("time") String time,
                                   @PathVariable("type") String type, HttpServletRequest request, HttpSession session) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("member", mService.findByid((String) session.getAttribute("userId")));
        mav.addObject("team",
                tService.findBytName(mService.findByid((String) session.getAttribute("userId")).getTName()));
        Cookie[] cookies = request.getCookies();
        FieldDTO field = fService.findByfName(selectField);
        String date = null;
        for (Cookie cookie : cookies) {
            System.out.println("쿠키 이름 : " + cookie.getName());
            if (cookie.getName().equals("date")) {
                date = cookie.getValue();
                mav.addObject("date", date);
            }
        }
        mav.addObject("selectField", field);
        mav.addObject("time", time);
        MemberDTO member = mService.findByid((String) session.getAttribute("userId"));
        Integer point = null;
        try {
            point = member.getHadPoint();
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        mav.addObject("point", point);
        if (type.equalsIgnoreCase("yellow")) {
            mav.addObject("reserveInfo", rService.findReserve(selectField, date, time));
        } else {
            mav.addObject("reserveInfo", null);
        }
        // TEST CODE
        System.out.println("=================== Sesssion ==================");
        System.out.println("세션 유저 아이디 : " + session.getAttribute("userId"));
        System.out.println("=================== Sesssion ==================");
        System.out.println();
        System.out.println("=============== Reserve Model Objects ===================");
//        System.out.println("member : " + mService.findByid((String) session.getAttribute("userId")));
        System.out.println("team : "
                + tService.findBytName(mService.findByid((String) session.getAttribute("userId")).getTName()));
        System.out.println("현재 선택된 필드 : " + field);
        System.out.println("현재 선택된 시간 : " + time);
        System.out.println("예약자의 타입 Green=A, Yellow=B : " + type);
        System.out.println("=============== Reserve Model Objects ===================");
        // END TEST


        mav.addObject("fNList", Utils.fNList);
        mav.addObject("latList", Utils.latList);
        mav.addObject("lonList", Utils.lonList);
        Utils.getAlarms(mav, mService.findByid((String) session.getAttribute("userId")));
        mav.addObject("uuid", UUID.randomUUID().toString());
        mav.addObject("type", type);
        mav.setViewName("/reserve");
        return mav;
    }

    @RequestMapping(value = "/display", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> display(String fileName) {
//      파일이 있는 경로
        log.info("fileName : " + fileName);
        if (fileName.equals(null)) {
            log.info("팀이 없습니다.");
            ResponseEntity<byte[]> result = null;
            return result;
        } else {
            File file = new File("c:/upload/" + fileName);
            log.info("file : " + file);
            ResponseEntity<byte[]> result = null;
            try {
                HttpHeaders header = new HttpHeaders();
                header.add("Content-Type", Files.probeContentType(file.toPath()));
                result = new ResponseEntity<byte[]>(FileCopyUtils.copyToByteArray(file), header, HttpStatus.OK);
            } catch (Exception e) {
                e.printStackTrace();
                // TODO: handle exception
            }
            return result;
        }
    }

    @RequestMapping(value = "/sortpopularAjax", method = RequestMethod.POST)
    @ApiOperation(value = "메인페이지 구장인기순으로 정렬", response = FieldDTO.class)
    @ResponseBody
    public ArrayList<FieldDTO> MainSortPopular(HttpSession session) throws Exception {
        log.info("sortpopularAjax 컨트롤러 들어옴,,,");
        ArrayList<FieldDTO> popularfield = null;

        popularfield = fService.findTop10ByOrderByLikeListCountDesc();
        log.info("popularfield : " + popularfield.get(9)); // V DTO잘찍힘
        log.info("popularfield : " + popularfield.get(0)); // V DTO잘찍힘
        log.info("popularfieldsize : " + popularfield.size()); // V 10
//        log.info("popularfieldsize : " + popularfield.get(9).getLikeListCount()); // V null
//        log.info("popularfieldsize : " + popularfield.get(0).getReviews().size()); // V null
//        log.info("popularfieldsize : " + popularfield.get(1).getReviews().size()); // V null
//        log.info("popularfieldsize : " + popularfield.get(2).getReviews().size()); // V null
//        log.info("popularfieldsize : " + popularfield.get(3).getReviews().size()); // V null
//        log.info("popularfieldsize : " + popularfield.get(4).getReviews().size()); // V null
//        log.info("popularfieldsize : " + popularfield.get(5).getReviews().size()); // V null
//        log.info("popularfieldsize : " + popularfield.get(6).getReviews().size()); // V null
//        log.info("popularfieldsize : " + popularfield.get(7).getReviews().size()); // V null
//        log.info("popularfieldsize : " + popularfield.get(8).getReviews().size()); // V null
//        log.info("popularfieldsize : " + popularfield.get(9).getReviews().size()); // V null
        log.info("popularfieldsize : " + popularfield.get(9).getLikeListCount()); // V null

        return popularfield;
    }

    @RequestMapping(value = "/sortlikeAjax", method = RequestMethod.POST)
    @ApiOperation(value = "메인페이지 내가 좋아요 누른순으로 정렬", response = FieldDTO.class)
    @ResponseBody
    public ArrayList<FieldDTO> MainSortLike(HttpSession session) throws Exception {
        log.info("sortlikeAjax 컨트롤러 들어옴,,,");
        ArrayList<FieldDTO> likefield = new ArrayList<FieldDTO>();
        MemberDTO member = mService.findByid((String) session.getAttribute("userId"));
        if (member != null) {
            for (int i = 0; i < member.getLikeFieldList().size(); i++) {
                log.info("찜한 구장 " + i + " : " + member.getLikeFieldList().get(i));
            }
            try {
                for (int i = 0; i < member.getLikeFieldList().size(); i++) {
                    likefield.add(fService.findByfName(member.getLikeFieldList().get(i)));
                }
            } catch (Exception e) {
                log.info("해당회원은 찜 한 구장이 없다이자식아 : " + e.getMessage());
                likefield = null;
            }
        }
//        log.info("likefield : " + likefield.get(0));
        return likefield;
    }

    @RequestMapping(value = "/sortreviewAjax", method = RequestMethod.POST)
    @ApiOperation(value = "메인페이지 리뷰 갯수순으로 정렬", response = FieldDTO.class)
    @ResponseBody
    public ArrayList<FieldDTO> MainSortReview(HttpSession session) throws Exception {
        log.info("sortreviewAjax 컨트롤러 들어옴,,,");
        ArrayList<FieldDTO> reviewfield = new ArrayList<FieldDTO>();
        reviewfield = fService.findTop12ByOrderByReviewsCountDesc();

        log.info("리뷰순정렬한컨트롤러 결과로 보낼 값 : " + reviewfield.get(0).getLikeListCount());
        log.info("리뷰순정렬한컨트롤러 결과로 보낼 값 : " + reviewfield.size());
        log.info("리뷰순정렬한컨트롤러 결과로 보낼 값 : " + reviewfield.get(11).getLikeListCount());

        return reviewfield;
    }


    @RequestMapping(value = "/ifUserHaveToWriteManner")
    @ApiOperation(value = "메인페이지 접근 시 해당 회원이 매너점수 기입할게 있는지 체크", response = MemberDTO.class)
    @ResponseBody
    public ArrayList<Manner> MannerOrNot(@RequestParam("userId") String userId) throws Exception {
        log.info("ifUserHaveToWriteManner 컨트롤러 들어옴,,,");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        ZonedDateTime nowSeoul = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        String parsedcurDateTime = nowSeoul.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); //현재시간을 String으로 파씽
        ReserveDTO reserve = new ReserveDTO();
        ArrayList<ReserveDTO> reservelist = new ArrayList<ReserveDTO>();
        int hours = nowSeoul.getHour();

        String nowDate = nowSeoul.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String nowDate2 = nowDate.replace("-", "/");
        Date format1 = new SimpleDateFormat("yyyy/MM/dd").parse(nowDate2);
        ArrayList<ReservationVO> volist = new ArrayList<ReservationVO>();
        ArrayList<Manner> manList = new ArrayList<>();
        try {
            String nickName = mService.findByid(userId).getNickName();
            String manner = null;

            ReservationDTO dto = rvService.findByid(nickName);
            log.info("처음 VO의 크기 : " + dto.getInfo().size());
            volist = dto.getInfo();
//            log.info("volist ================ : " + volist);
            for (int i = 0; i < volist.size(); i++) {
                ReservationVO vo = new ReservationVO();
                Manner man = new Manner();
                String[] timeArray = dto.getInfo().get(i).getTime().split("시 - ");
                String[] timeArray2 = timeArray[1].split("시");
                vo = dto.getInfo().get(i);
                String voDate = vo.getDate().replace("-", "/");
                Date format2 = new SimpleDateFormat("yyyy/MM/dd").parse(voDate);
                reserve = rService.findReserve(vo.getField(), vo.getDate(), vo.getTime());
                if (vo.getState().equals("매칭 완료") && !vo.getType().equals("홈 & 어웨이")) {
                    if ((vo.getDate().equals(nowDate) && hours >= Integer.parseInt(timeArray2[0])) || (format1.getTime() - format2.getTime()) / (24 * 60 * 60) > 0) {
                        manner = vo.getManner();
                        if (manner == null) {
                            if (vo.getType().equals("홈")) {
                                man.setNickName(reserve.getNickNameB()); //이걸로찾아야함
                            } else if (vo.getType().equals("어웨이")) {
                                man.setNickName(reserve.getNickNameA()); //이걸로찾아야함
                            }
                            man.setField(vo.getField());
                            man.setDate(vo.getDate());
                            man.setTime(vo.getTime());
                            manList.add(man);
                            manner = "매너알람완료";
                            vo.setManner(manner);
                            dto.getInfo().set(i, vo);
                            volist.set(i, vo);
                            reservelist.add(reserve);
                        }
                    }
                }
            }

            dto.setInfo(volist);
//            log.info("========================== : " + dto);
            rvService.save(dto);
//            log.info("컨트롤러에서 패치로 보낼 값 : " + manList);
            log.info("컨트롤러에서 패치로 보낼 값의 크기 : " + manList.size());
//            log.info("컨트롤러에서 리저브리스트의 크기 : " + reservelist.size());
        } catch (Exception e) {
            log.info("ifUserHaveToWriteManner 오류 : " + e.getMessage());
        }
//        log.info("첫번째 결과 : " + manList.get(0));
        return manList;
    }

    @RequestMapping(value = "/alarmAcceptToAwayAjax")
    @ApiOperation(value = "도전신청 받았는지 알람 수신", response = MemberDTO.class)
    @ResponseBody
    public ReserveDTO alarmAcceptToAwayAjax(@RequestParam("awayTeam") String awayTeam, @RequestParam("date") String date,
                                            @RequestParam("time") String time, @RequestParam("field") String field, HttpSession session) throws Exception {
        log.info("alarmAcceptToAwayAjax 컨트롤러들어옴.,,,,,,,");
//        log.info("alarmAcceptToAwayAjax 컨트롤러 awayTeam : " + awayTeam); //v
        String myTeamName = mService.findByid((String) session.getAttribute("userId")).getTName();
        ReserveDTO rdto = new ReserveDTO();
        rdto.setTime(time);
        rdto.setField(field);
        rdto.setDate(date);
        rdto.setNameA(myTeamName);
        rdto.setNameB(awayTeam);
        log.info("컨트롤러에서 패치로 보낼 값 : " + rdto);
        return rdto;
    }

    @ResponseBody
    @RequestMapping(value = "/movetoreservation")
    @ApiOperation(value = "알람에서 예약목록 페이지로 이동", response = MemberDTO.class)
    public void moveToReservation(@RequestParam("message") String message, HttpSession session) {
        log.info("movetoreservation 컨트롤러 들어옴 ,,,");
        log.info("파라미터 메세지 확인 :" + message);
        MemberDTO mem = mService.findByid((String) session.getAttribute("userId"));
        int readcnt = 0;
        log.info("초반점검1 : {}", (mem.getAlarms().get(0).getMessage()).replace("\n", ""));
        log.info("초반점검2 : {}", message);
        int size = mem.getAlarms().size();
        for (int i = 0; i < size; i++) {
            if ((mem.getAlarms().get(i).getMessage()).replace("\n", "").equals(message)) {
                mem.getAlarms().get(i).setIsRead("읽음");
                try {
                    readcnt = mem.getReadCount() + 1;
                } catch (Exception e) {
                    log.info(Arrays.toString(e.getStackTrace()));
                    readcnt = 1;
                }
                mem.setReadCount(readcnt);
                mService.save(mem);
            }
        }
    }
}