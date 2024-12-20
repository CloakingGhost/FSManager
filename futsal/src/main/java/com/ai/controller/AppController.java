package com.ai.controller;

import com.ai.domain.*;
import com.ai.service.*;
import com.google.firebase.messaging.FirebaseMessagingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Slf4j
@RestController
@Api(tags = {"모바일 앱 관련 1/2 API"})
@RequestMapping(value = "/app")
public class AppController {
    @Autowired
    FieldService fService;

    @Autowired
    MemberService mService;

    @Autowired
    WeatherService wService;

    @Autowired
    ReserveService rService;

    @Autowired
    ReservationService rvService;

    @Autowired
    ReserveListService rlService;

    @Autowired
    TeamService tService;

    @Autowired
    CommunityService cService;

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    SequenceGeneratorService sService;

    // 두 좌표 사이의 거리를 구하는 함수
    // dsitance(첫번쨰 좌표의 위도, 첫번째 좌표의 경도, 두번째 좌표의 위도, 두번째 좌표의 경도)
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



//    @ApiOperation(value = "회원가입")
    @RequestMapping(value = "/fieldinfo", method = RequestMethod.POST)
    public ArrayList<FieldDTO> fieldinfo(@RequestParam("lat") double lat, @RequestParam("lon") double lon) {
        ArrayList<FieldDTO> fieldList = fService.findAll();
        ArrayList<Distance> disList = new ArrayList<>();
        ArrayList<FieldDTO> fList = new ArrayList<>();
        // 각 구장별 거리
        for (int i = 0; i < fieldList.size(); i++) {
            double dist = distance(lat, lon,
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
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < fieldList.size(); j++) {
                if (disList.get(i).getField().equals(fieldList.get(j).getFName())) {
                    fList.add(fieldList.get(j));
                }
            }
        }
        return fList;
    }

    @RequestMapping(value = "/getFieldName", method = RequestMethod.GET)
    public ArrayList<FieldDTO> get_fieldName() {
        return fService.findAll();
    }

    ;

    @RequestMapping(value = "/locfieldinfo", method = RequestMethod.POST)
    public ArrayList<LocationFieldDTO> get_locfieldinfo(@RequestParam("lat") double lat, @RequestParam("lon") double lon) {
        ArrayList<FieldDTO> fieldList = fService.findAll();
        ArrayList<Distance> disList = new ArrayList<>();
        ArrayList<LocationFieldDTO> fList = new ArrayList<>();
        // 각 구장별 거리t
        for (int i = 0; i < fieldList.size(); i++) {
            double dist = distance(lat, lon,
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
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < fieldList.size(); j++) {
                if (disList.get(i).getField().equals(fieldList.get(j).getFName())) {
                    double dist = distance(lat, lon, Double.parseDouble(fieldList.get(j).getLatitude()), Double.parseDouble(fieldList.get(j).getLongitude()));
                    LocationFieldDTO locField = new LocationFieldDTO();
                    locField.setField(fieldList.get(j));
                    locField.setDistance(dist);
                    fList.add(locField);
                }
            }
        }
        return fList;
    }

    @RequestMapping(value = "/postRegSearch")
    public ArrayList<LocationFieldDTO> post_reg_search(@RequestParam("fName") String fName, @RequestParam("lat") double latitude, @RequestParam("lon") double longitude) {
        ArrayList<LocationFieldDTO> locList = new ArrayList<>();
        ArrayList<FieldDTO> dtoList = fService.findAll();
        List<FieldDTO> NAfields = new ArrayList<>();
        for (int i = 0; i < dtoList.size(); i++) {
            if (dtoList.get(i).getFName().contains(fName) || dtoList.get(i).getFAddress().contains(fName)) {
                NAfields.add(dtoList.get(i));
            }
        }

        ArrayList<FieldDTO> fieldList = fService.findAll();
        ArrayList<Distance> disList = new ArrayList<>();
        ArrayList<LocationFieldDTO> fList = new ArrayList<>();
        for (int i = 0; i < fieldList.size(); i++) {
            double dist = distance(latitude, longitude,
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
        for (int i = 0; i < fieldList.size(); i++) {
            for (int j = 0; j < fieldList.size(); j++) {
                if (disList.get(i).getField().equals(fieldList.get(j).getFName())) {
                    double dist = distance(latitude, longitude, Double.parseDouble(fieldList.get(j).getLatitude()), Double.parseDouble(fieldList.get(j).getLongitude()));
                    LocationFieldDTO locField = new LocationFieldDTO();
                    locField.setField(fieldList.get(j));
                    locField.setDistance(dist);
                    fList.add(locField);
                }
            }
        }
        for (int i = 0; i < fList.size(); i++) {
            for (int j = 0; j < NAfields.size(); j++) {
                if (fList.get(i).getField().getFName().equals(NAfields.get(j).getFName())) {
                    locList.add(fList.get(i));
                }
            }
            if (locList.size() == 20) {
                break;
            }
        }
        return locList;
    }

    @RequestMapping(value = "/fieldone", method = RequestMethod.POST)
    public AppFieldDTO fieldTest(@RequestParam("fName") String fName) {
        AppFieldDTO appData = new AppFieldDTO();
        FieldDTO field = fService.findByfName(fName);
        WeatherDTO weather = wService.findByid(field.getFName());
        ArrayList<WeatherVO> weatherList = weather.getWeather();
        LinkedHashMap<String, String> timeMap = new LinkedHashMap<String, String>();
        ZonedDateTime nowSeoul = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        String date = nowSeoul.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        ArrayList<ReserveDTO> reserveList = new ArrayList<ReserveDTO>();
        int hours = nowSeoul.getHour();
        for (int j = 0; j < 12; j++) {
            timeMap.put(String.format("%d시 - %d시", 2 * j, (2 * j) + 2), "null");
        }
        String[] timeArray = timeMap.keySet().toArray(new String[timeMap.size()]);

        try {
            reserveList = rService.findByField(fName);
            for (int i = 0; i < reserveList.size(); i++) {
                if (reserveList.get(i).getDate().equals(date)) {
                    try {
                        if (reserveList.get(i).getType().equalsIgnoreCase("part")) {
                            timeMap.replace(reserveList.get(i).getTime(), reserveList.get(i).getState());
                        }
                        if (reserveList.get(i).getType().equalsIgnoreCase("all") || reserveList.get(i).getState().equalsIgnoreCase("B")) {
                            timeMap.replace(reserveList.get(i).getTime(), "full");
                        }
                    } catch (Exception e) {
                        System.out.println("Field Controller Error : " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("해당 구장의 예약 정보가 없음!");
        }
        for (int p = 0; p < timeArray.length; p++) {
            String[] timeArray2 = timeArray[p].split("시 - ");
            String[] timeArray3 = timeArray2[1].split("시");
            if (hours >= Integer.parseInt(timeArray3[0].toString())) {
                timeMap.replace(timeArray[p].toString(), "full");
            }
        }
        appData.setField(field);
        appData.setWeatherList(weatherList);
        appData.setTimeMap(timeMap);
        return appData;
    }

    @RequestMapping(value = "/postSearchField", method = RequestMethod.POST)
    public FieldDTO post_searchField(@RequestParam("fName") String fName) {
        return fService.findByfName(fName);
    }


    @RequestMapping(value = "/getPopular", method = RequestMethod.GET)
    public ArrayList<FieldDTO> get_popular() {
        ArrayList<FieldDTO> fList = fService.findTop10ByOrderByLikeListCountDesc();
        ArrayList<FieldDTO> fList2 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            fList2.add(fList.get(i));
        }
        return fList2;
    }

    @RequestMapping(value = "/getComment", method = RequestMethod.GET)
    public ArrayList<FieldDTO> get_comment() {
        ArrayList<FieldDTO> fList = fService.findTop12ByOrderByReviewsCountDesc();
        ArrayList<FieldDTO> fList2 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            fList2.add(fList.get(i));
        }
        return fList2;
    }


    @RequestMapping(value = "/checkMember", method = RequestMethod.POST)
    public MemberDTO post_memberInfo(@RequestParam("id") String id, @RequestParam("platform") String platform, @RequestParam("fireBaseToken") String fireBaseToken) {
        MemberDTO dto = new MemberDTO();
        try {
            dto = mService.findByidAndPlatform(id, platform);
            dto.setFireBaseToken(fireBaseToken);
            mService.save(dto);
            log.info(dto.getNickName() + " 회원이 로그인");
        } catch (Exception e) {
            log.info("앱에서 존재하지 않는 회원이 로그인 시도");
            dto = null;
        }
        return dto;
    }

    @RequestMapping(value = "/postCheckMember", method = RequestMethod.POST)
    public ArrayList<String> post_check_member(@RequestParam("nickName") String nickName, @RequestParam("phoneNo") String phoneNo) {
        ArrayList<String> result = new ArrayList<>();
        log.info("앱에서 회원가입 버튼 클릭시 닉네임, 전화번호 중복여부 파악 중..");
        try {
            MemberDTO member = mService.findByNickName(nickName);
            log.info("중복된 닉네임 :: " + member.getNickName());
            result.add("닉네임 중복");
            return result;
        } catch (Exception e) {
            try {
                MemberDTO member = mService.findByPhoneNo(phoneNo);
                log.info("중복된 전화번호 :: " + member.getPhoneNo());
                result.add("전화번호 중복");
                return result;
            } catch (Exception e2) {
                if (nickName == null || nickName.equals("")) {
                    result.add("닉네임 입력 없음");
                    log.info("닉네임 입력 null 값");
                    return result;
                }
                if (phoneNo == null || phoneNo.equals("")) {
                    result.add("전화번호 입력 없음");
                    log.info("전화번호 입력 null 값");
                    return result;
                }
                result.add("중복 없음");
                log.info("멤버 정보 중복없음, 회원가입 진행");
            }
        }
        return result;
    }

    @RequestMapping(value = "/postJoinMember")
    public MemberDTO post_join_member(@RequestParam("id") String id, @RequestParam("name") String name, @RequestParam("nickName") String nickName, @RequestParam("sex") String sex, @RequestParam("phoneNo") String phoneNo, @RequestParam("regDate") String regDate, @RequestParam("platform") String platform, @RequestParam("fireBaseToken") String fireBaseToken) {
        MemberDTO member = new MemberDTO();
        member.setId(id);
        member.setName(name);
        member.setNickName(nickName);
        member.setSex(sex);
        member.setPhoneNo(phoneNo);
        member.setRegDate(regDate);
        member.setPlatform(platform);
        member.setHadPoint(1000000);
        member.setAlarmCount(0);
        member.setReadCount(0);
        member.setFireBaseToken(fireBaseToken);
        member.setBlockList(new ArrayList<String>());
        log.info("회원가입한 유저 정보 : " + member);
        mService.insert(member);
        return member;
    }

    @RequestMapping(value = "/postdate", method = RequestMethod.POST)
    public LinkedHashMap<String, String> postTime(@RequestParam("field") String fName, @RequestParam("date") String date) {
        LinkedHashMap<String, String> timeMap = new LinkedHashMap<String, String>();
        ArrayList<ReserveDTO> reserveList = new ArrayList<ReserveDTO>();
        ZonedDateTime nowSeoul = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        int hours = nowSeoul.getHour();
        String now = nowSeoul.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        for (int j = 0; j < 12; j++) {
            timeMap.put(String.format("%d시 - %d시", 2 * j, (2 * j) + 2), "null");
        }
        String[] timeArray = timeMap.keySet().toArray(new String[timeMap.size()]);

        try {
            reserveList = rService.findByField(fName);
            for (int i = 0; i < reserveList.size(); i++) {
                if (reserveList.get(i).getDate().equals(date)) {
                    try {
                        if (reserveList.get(i).getType().equalsIgnoreCase("part")) {
                            timeMap.replace(reserveList.get(i).getTime(), reserveList.get(i).getState());
                        }
                        if (reserveList.get(i).getType().equalsIgnoreCase("all") || reserveList.get(i).getState().equalsIgnoreCase("B")) {
                            timeMap.replace(reserveList.get(i).getTime(), "full");
                        }
                    } catch (Exception e) {
                        System.out.println("Field Controller Error : " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("해당 구장의 예약 정보가 없음!");
        }
        if (now.equals(date)) {
            System.out.println("현재 날짜와 선택된 날짜가 같음!");
            for (int p = 0; p < timeArray.length; p++) {
                String[] timeArray2 = timeArray[p].split("시 - ");
                String[] timeArray3 = timeArray2[1].split("시");
                if (hours >= Integer.parseInt(timeArray3[0].toString())) {
                    timeMap.replace(timeArray[p].toString(), "full");
                }
            }
        }
        return timeMap;
    }

    @RequestMapping(value = "/reserveInsert", method = RequestMethod.POST)
    public ReserveDTO insertReserve(@RequestParam("field") String fName,
                                    @RequestParam("price") Integer price,
                                    @RequestParam("date") String date,
                                    @RequestParam("time") String time,
                                    @RequestParam("phoneNo") String phoneNo,
                                    @RequestParam("type") String type,
                                    @RequestParam("state") String state,
                                    @RequestParam("nickNameA") String nickNameA,
                                    @RequestParam("nameA") String tName) {
        ReserveDTO reserve = new ReserveDTO();
        ZonedDateTime nowSeoul = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        System.out.println("Seoul Time = " + nowSeoul);
        ReservationDTO reservationDTO = new ReservationDTO();
        String nowDate = nowSeoul.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        reserve.setField(fName);
        if (price == 100000) {
            reserve.setPrice("100,000 P");
        }
        reserve.setDate(date);
        reserve.setTime(time);
        reserve.setPhoneNo(phoneNo);
        reserve.setType(type);
        reserve.setState(state);
        reserve.setNickNameA(nickNameA);
        reserve.setNameA(tName);
        reserve.setDateA(nowDate);
        if (state.equals("B")) {
            reserve.setNickNameB(nickNameA);
            reserve.setNameB(tName);
            reserve.setDateB(nowDate);
        }
        // 디비에서 돈빼야함
        MemberDTO member = mService.findByNickName(nickNameA);
        member.setHadPoint(member.getHadPoint() - price);
        mService.save(member);
        rService.insert(reserve);

        ReservationVO reservation = new ReservationVO();
        ArrayList<ReservationVO> reservationList = new ArrayList<ReservationVO>();

        log.info("앱에서 예약 신청중 ::: 타입 확인  ::: " + reserve.getType());
        if (reserve.getType().equalsIgnoreCase("part")) {
            reservation.setDate(reserve.getDate());
            reservation.setRegDate(reserve.getDateA());
            reservation.setState("매칭 대기중");
            reservation.setType("홈");
        }
        if (reserve.getType().equalsIgnoreCase("all")) {
            reservation.setDate(reserve.getDate());
            reservation.setRegDate(reserve.getDateA());
            reservation.setType("홈 & 어웨이");
            reservation.setState("매칭 완료");
        }
        reservation.setTime(reserve.getTime());
        reservation.setField(reserve.getField());
        reservation.setPrice(reserve.getPrice());
        reservation.setPrice(fService.findByfName(fName).getFPic1());
        try {
            reservationDTO = rvService.findByid(nickNameA);
            reservationList = reservationDTO.getInfo();
            if(reservationList.get(0) != null){
                reservationList.add(reservation);
            } else {
                reservationList = new ArrayList<>();
                reservationList.add(reservation);
            }
            reservationDTO.setInfo(reservationList);
            log.info("앱에서 신청한 예약이 저장된 Reservation (예약 정보 있는경우) :: " + reservationDTO);
            rvService.save(reservationDTO);
        } catch (Exception e) {
            log.info("Reservation 디비에 등록된 정보가 없습니다.");
            reservationList.add(reservation);
            reservationDTO = new ReservationDTO(nickNameA, reservationList);
            log.info("앱에서 신청한 예약이 저장된 Reservation (예약 정보 없는경우) :: " + reservationDTO);
            rvService.insert(reservationDTO);
        }
        return reserve;
    }

    @RequestMapping(value = "/reserveSave", method = RequestMethod.POST)
    public ReserveDTO saveReserve(@RequestParam("field") String field, @RequestParam("date") String fDate, @RequestParam("time") String fTime,
                                  @RequestParam("nickNameB") String nickNameB, @RequestParam("tNameB") String tNameB) {
        ReserveDTO reserve = rService.findReserve(field, fDate, fTime);
        reserve.setNickNameB(nickNameB);
        reserve.setType("all");
        reserve.setState("B");
        reserve.setNameB(tNameB);
        ZonedDateTime nowSeoul = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        String nowDate = nowSeoul.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        reserve.setDateB(nowDate);
        rService.save(reserve);

        ReservationVO reservation = new ReservationVO();
        ArrayList<ReservationVO> reservationList = new ArrayList<ReservationVO>();
        if (reserve.getNickNameA().equals(reserve.getNickNameB()) || reserve.getNameA().equals(reserve.getNameB())) {
            reservation.setDate(reserve.getDate());
            reservation.setRegDate(reserve.getDateA());
            reservation.setTime(reserve.getTime());
            reservation.setField(reserve.getField());
            reservation.setPrice(reserve.getPrice());
            reservation.setType("홈 & 어웨이");
            reservation.setState("매칭 완료");
            reservation.setFieldPic(fService.findByfName(field).getFPic1());
            reservationList.add(reservation);
        } else {
            reservation.setDate(reserve.getDate());
            reservation.setRegDate(reserve.getDateB());
            reservation.setTime(reserve.getTime());
            reservation.setField(reserve.getField());
            reservation.setPrice(reserve.getPrice());
            reservation.setType("어웨이");
            reservation.setState("매칭 대기중");
            reservation.setFieldPic(fService.findByfName(field).getFPic1());
            reservationList.add(reservation);

            ReserveListDTO rl = new ReserveListDTO();
            rl.setField(reserve.getField());
            rl.setPrice(reserve.getPrice());
            rl.setDate(reserve.getDate());
            rl.setTime(reserve.getTime());
            rl.setTeam(reserve.getNameB());
            rl.setNickName(reserve.getNickNameB());
            rl.setRegDate(nowDate);

            rlService.insert(rl);


        }

        ReservationDTO reservationDTO = rvService.findByid(nickNameB);
        for (int i = 0; i < reservationDTO.getInfo().size(); i++) {
            if (reservationDTO.getInfo().get(i).getField().equals(reservationList.get(0).getField())
                    && reservationDTO.getInfo().get(i).getTime().equals(reservationList.get(0).getTime())
                    && reservationDTO.getInfo().get(i).getDate().equals(reservationList.get(0).getDate())) {
                reservationDTO.getInfo().remove(i);
                log.info("기존 예약 제거");
            }
        }
        reservationDTO.getInfo().addAll(reservationList);
        rvService.save(reservationDTO);
        return reserve;
    }

    @RequestMapping(value = "/postCheck", method = RequestMethod.POST)
    public ArrayList<String> checkReserve(@RequestParam("field") String fName, @RequestParam("date") String date, @RequestParam("time") String time) {
        ReserveDTO reserve = rService.findReserve(fName, date, time);
        ArrayList<String> checkList = new ArrayList<>();
        checkList.add(reserve.getNickNameA());
        checkList.add(reserve.getNameA());
        return checkList;
    }

    @RequestMapping(value = "/postHomeTeam", method = RequestMethod.POST)
    public TeamDTO postHomeTeam(@RequestParam("field") String field, @RequestParam("date") String date, @RequestParam("time") String time) {
        TeamDTO homeTeam = new TeamDTO();
        homeTeam = tService.findBytName(rService.findReserve(field, date, time).getNameA());
        return homeTeam;
    }

    @RequestMapping(value = "/postJoinTeam", method = RequestMethod.POST)
    public ArrayList<String> post_join_team(@RequestParam("tName") String tName, @RequestParam("id") String id) {
        ArrayList<String> result = new ArrayList<>();
        TeamDTO team = tService.findBytName(tName);
        MemberDTO member = mService.findByid(id);
        ArrayList<String> applymem = null;
        try {
            applymem = team.getApplyMember();
            for (String apply : applymem) {
                if (apply.equals(member.getNickName())) {
                    result.add("이미 있음");
                    return result;
                }
            }
            applymem.add(member.getNickName());
            result.add("신청 성공");
        } catch (Exception e) {
            log.info("해당 팀에 신청한명단이 없습니다.");
            applymem = new ArrayList<String>();
            applymem.add(member.getNickName());
            result.add("신청 성공");
        }
        team.setApplyMember(applymem);
        tService.save(team);
        return result;
    }

    @RequestMapping(value = "/postCheckTeam", method = RequestMethod.POST)
    public ArrayList<String> post_check_team(@RequestParam("tName") String tName, @RequestParam("id") String id) {
        ArrayList<String> result = new ArrayList<>();
        TeamDTO team = tService.findBytName(tName);
        MemberDTO member = mService.findByid(id);
        ArrayList<String> applymem = null;
        try {
            applymem = team.getApplyMember();
            log.info(applymem.get(0));
            if (applymem.size() > 0) {
                for (int i = 0; i < applymem.size(); i++) {
                    if (applymem.get(i).equals(member.getNickName())) {
                        result.add("이미 신청");
                    }
                }
            }
        } catch (Exception e) {
            result.add("신청 완료");
        }
        return result;
    }

    @RequestMapping(value = "/postCancelTeam", method = RequestMethod.POST)
    public ArrayList<String> post_Cancel_team(@RequestParam("tName") String tName, @RequestParam("id") String id) {
        ArrayList<String> result = new ArrayList<>();
        TeamDTO team = tService.findBytName(tName);
        MemberDTO member = mService.findByid(id);
        ArrayList<String> applymem = null;
        try {
            applymem = team.getApplyMember();
            if (applymem.size() > 0) {
                for (int i = 0; i < applymem.size(); i++) {
                    if (applymem.get(i).equals(member.getNickName())) {
                        applymem.remove(i);
                        result.add("취소 성공");
                        team.setApplyMember(applymem);
                        tService.save(team);
                    }
                }
            }
        } catch (Exception e) {
            result.add("취소 실패");
        }
        return result;
    }


    @RequestMapping(value = "/postWeather", method = RequestMethod.POST)
    public WeatherVO postWeather(@RequestParam("time") String time, @RequestParam("date") String date,
                                 @RequestParam("field") String field) {
        WeatherVO weather = new WeatherVO();
        Integer thistime = 0;
        switch (time) {
            case "0시 - 2시":
                thistime = 0;
                break;
            case "2시 - 4시":
                thistime = 0;
                break;
            case "4시 - 6시":
                thistime = 3;
                break;
            case "6시 - 8시":
                thistime = 6;
                break;
            case "8시 - 10시":
                thistime = 6;
                break;
            case "10시 - 12시":
                thistime = 9;
                break;
            case "12시 - 14시":
                thistime = 12;
                break;
            case "14시 - 16시":
                thistime = 12;
                break;
            case "16시 - 18시":
                thistime = 15;
                break;
            case "18시 - 20시":
                thistime = 18;
                break;
            case "20시 - 22시":
                thistime = 18;
                break;
            case "22시 - 24시":
                thistime = 21;
                break;
        }
        WeatherDTO dto = wService.findByid(field);
        WeatherVO lastvo = dto.getWeather().get(39); // DB에 있는 마지막 날씨정보
        LocalDate localDate1 = LocalDate.parse(lastvo.getDate());
        LocalDate localDate2 = LocalDate.parse(date);
        log.info("날짜빼기결과 : " + localDate1.isAfter(localDate2));
        boolean result = localDate1.isAfter(localDate2);
        if (result == true) { // 날씨정보볼수있음
            try {
                for (int i = 0; i < dto.getWeather().size(); i++) {
                    if (dto.getWeather().get(i).getDate().equals(date)) {
                        if (dto.getWeather().get(i).getTime().equals(thistime)) {
                            weather = dto.getWeather().get(i);
                        }
                    }
                }
                log.info("컨트롤러 결과 : " + weather);
            } catch (Exception e) {
                log.info("해당하는 날씨가 없음 ! : " + e.getMessage());
            }
        } else { // 해당날짜에 대한 날씨DB존재 X
            weather = null;
        }
        return weather;
    }

    @RequestMapping(value = "/reserveSave1", method = RequestMethod.POST)
    public ReserveDTO post_save1(@RequestParam("name") String name, @RequestParam("field") String fName, @RequestParam("date") String date, @RequestParam("time") String time) {
        ReservationDTO rv = new ReservationDTO();
        ReserveDTO dto = rService.findReserve(fName, date, time);
        ZonedDateTime nowSeoul = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        String nowDate = nowSeoul.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        try {
            rv = rvService.findByid(name);
            log.info("앱에서 홈 & 어웨이 클릭 시 예약정보 확인됨! 예약자 : " + rv.getId());
            if(dto.getNickNameA().equals(name)){
                log.info("앱에서 홈 & 어웨이 변경 하려는 사람이 본인임");
                ReservationVO rvo = new ReservationVO();
                for (int i = 0; i < rv.getInfo().size(); i++) {
                    if (rv.getInfo().get(i).getDate().equals(date) && rv.getInfo().get(i).getTime().equals(time) && rv.getInfo().get(i).getField().equals(fName)) {
                        rvo = rv.getInfo().get(i);
                        rvo.setState("매칭 완료");
                        rvo.setType("홈 & 어웨이");
                        rv.getInfo().set(i, rvo);
                        rvService.save(rv);
                    }
                }
            } else {
                log.info("앱에서 홈 & 어웨이 변경 하려는 사람이 팀원임");
                ArrayList<ReservationVO> teamRvoList = rv.getInfo();
                ReservationVO teamRvo = new ReservationVO();
                teamRvo.setDate(date);
                teamRvo.setRegDate(nowDate);
                teamRvo.setTime(time);
                teamRvo.setField(fName);
                teamRvo.setPrice(dto.getPrice());
                teamRvo.setState("매칭 완료");
                teamRvo.setType("홈 & 어웨이");
                teamRvoList.add(teamRvo);
                rv.setInfo(teamRvoList);
                rvService.save(rv);

                ReservationDTO homeRv = rvService.findByid(dto.getNickNameA());
                ReservationVO homeRvo = new ReservationVO();
                for (int i = 0; i < homeRv.getInfo().size(); i++) {
                    if (homeRv.getInfo().get(i).getDate().equals(date) && homeRv.getInfo().get(i).getTime().equals(time) && homeRv.getInfo().get(i).getField().equals(fName)) {
                        homeRvo = homeRv.getInfo().get(i);
                        homeRvo.setState("매칭 완료");
                        homeRvo.setType("홈 & 어웨이");
                        homeRv.getInfo().set(i, homeRvo);
                        rvService.save(homeRv);
                        log.info("앱에서 홈 & 어웨이 변경 시, 팀원의 정보도 변경됬고, 기존 신청자도 변경됨.");
                    }
                }
            }
        } catch (Exception e){
            log.info("앱에서 홈 & 어웨이 클릭 시 팀원이 눌렀으며, 처음으로 예약하는 중");
            ReservationDTO newRv = new ReservationDTO();
            newRv.setId(name);
            ReservationVO newRvo = new ReservationVO();
            ArrayList<ReservationVO> rvoList = new ArrayList<>();
            newRvo.setDate(date);
            newRvo.setRegDate(nowDate);
            newRvo.setTime(time);
            newRvo.setField(fName);
            newRvo.setPrice(dto.getPrice());
            newRvo.setState("매칭 완료");
            newRvo.setType("홈 & 어웨이");
            rvoList.add(newRvo);
            newRv.setInfo(rvoList);
            rvService.insert(newRv);

            ReservationDTO homeRv = rvService.findByid(dto.getNickNameA());
            ReservationVO homeRvo = new ReservationVO();
            for (int i = 0; i < homeRv.getInfo().size(); i++) {
                if (homeRv.getInfo().get(i).getDate().equals(date) && homeRv.getInfo().get(i).getTime().equals(time) && homeRv.getInfo().get(i).getField().equals(fName)) {
                    homeRvo = homeRv.getInfo().get(i);
                    homeRvo.setState("매칭 완료");
                    homeRvo.setType("홈 & 어웨이");
                    homeRv.getInfo().set(i, homeRvo);
                    rvService.save(homeRv);
                    log.info("앱에서 홈 & 어웨이 변경 시, 팀원의 정보도 변경됬고, 기존 신청자도 변경됨.");
                }
            }
        }
        dto.setNickNameB(name);
        dto.setNameB(mService.findByNickName(name).getTName());
        dto.setDateB(nowDate);
        dto.setType("all");
        dto.setState("B");
        rService.save(dto);
        return dto;
    }

    @RequestMapping(value = "/reserveSave2", method = RequestMethod.POST)
    public ArrayList<String> post_save2(@RequestParam("name") String name, @RequestParam("field") String fName, @RequestParam("date") String date, @RequestParam("time") String time) {
        log.info("앱에서 구장화면을 통해 도전 신청 ::");
        ArrayList<String> res = new ArrayList<>();
        ArrayList<ReserveListDTO> checkList = rlService.findByFieldAndDateAndTime(fName, date, time);
        ArrayList<ReservationVO> rvoList = new ArrayList<>();
        ReservationDTO rv = new ReservationDTO();
        ReservationVO rvo = new ReservationVO();
        for (int i = 0; i < checkList.size(); i++) {
            if (checkList.get(i).getNickName().equals(name)) {
                res.add("중복 있음");
                log.info("이미 도전신청한 구장임을 알림");
            }
        }
        if (res.size() == 0) {
            String tName = mService.findByNickName(name).getTName();
            String price = fService.findByfName(fName).getFPrice1() == 100000 ? "100,000 P" : "120,000 P";
            ZonedDateTime nowSeoul = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
            String nowDate = nowSeoul.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            // Reservation insert
            rv = rvService.findByid(name); // name -> 어웨이팀닉네임
            try {
                log.info("예약 정보 있는지 닉네임 확인 (있으면 보임):: " +rv.getId());
            } catch (Exception e){
                log.info("신규 회원이 앱에서 어웨이로 예약 중");
            }
            rvo.setDate(date);
            rvo.setRegDate(nowDate);
            rvo.setTime(time);
            rvo.setField(fName);
            rvo.setPrice(price);
            rvo.setFieldPic(fService.findByfName(fName).getFPic1());
            rvo.setState("매칭 대기중");
            rvo.setType("어웨이");
            try {
                rvoList = rv.getInfo();
                rvoList.add(rvo);
                rv.setInfo(rvoList);
                rvService.save(rv);
            } catch (Exception e) {
                // 처음 예약할 경우
                rvoList.add(rvo);
                ReservationDTO rv2 = new ReservationDTO();
                rv2.setId(name);
                rv2.setInfo(rvoList);
                rvService.insert(rv2);
            }

            // Reserve save
            ReserveDTO dto = rService.findReserve(fName, date, time);

            // ReserveList insert
            ReserveListDTO rvl = new ReserveListDTO();
            rvl.setField(fName);
            rvl.setPrice(price);
            rvl.setDate(date);
            rvl.setTime(time);
            rvl.setTeam(tName);
            rvl.setNickName(name);
            rvl.setRegDate(nowDate);
            rlService.insert(rvl);

            // yuki start
            // 어웨이팀(tName)가 도전할때, 홈팀(dto.getNameA())한테 알람보내기
            TeamDTO hometeam = tService.findBytName(dto.getNameA());
            MemberDTO hometeamreservationmember = mService.findByNickName(dto.getNickNameA());
            AlarmVO alarm = new AlarmVO();
            alarm.setSender(tName);
            alarm.setReceiver(dto.getNameA());
            alarm.setSendDate(nowDate);
            alarm.setIsRead("안읽음");
            alarm.setAlarmType("홈");
            alarm.setMessage(fName + "\n" + date + " " + time + "경기\n도전팀 : " + tName);
            ArrayList<AlarmVO> hometeamMemalarms = new ArrayList<>();
            try {
                hometeamreservationmember.setAlarmCount(hometeamreservationmember.getAlarmCount() + 1);
                hometeamMemalarms = hometeamreservationmember.getAlarms();
                hometeamMemalarms.add(alarm);
            } catch (Exception e) {
                hometeamMemalarms = new ArrayList<>();
                hometeamMemalarms.add(alarm);
            }
            mService.save(hometeamreservationmember);
            Manner resManner = new Manner();
            resManner.setUnreadalarmcount(hometeamreservationmember.getAlarmCount() - hometeamreservationmember.getReadCount());
            resManner.setMessage(fName + "\n" + date + " " + time + "경기\n도전팀 : " + tName);
            simpMessagingTemplate.convertAndSend("/queue/home/" + hometeamreservationmember.getNickName(), resManner);
            // yuki end

            // FireBase
            Firebase firebase = new Firebase();
            if (hometeamreservationmember.getFireBaseToken() != null) {
                try {
                    log.info("어웨이 팀 도전 알림 성공 : " + firebase.sendToToken(hometeamreservationmember.getFireBaseToken(), "도전 신청이 도착했어요", "홈 >> 예약에서 확인하기"));
                } catch (FirebaseMessagingException e) {
                    log.info("어웨이 팀 도전 알림 실패 : " + e.getMessage());
                }
            }
            res.add("중복 없음");
        }

        return res;
    }


    @RequestMapping(value = "/getMannerList")
    public ArrayList<TeamDTO> get_mannerList() {
        ArrayList<TeamDTO> tList = tService.findAll();
        Comparator<TeamDTO> comparator = new Comparator<TeamDTO>() {
            @Override
            public int compare(TeamDTO a, TeamDTO b) {
                return b.getTManner() - a.getTManner();
            }
        };
        Collections.sort(tList, comparator);
        return tList;
    }

    @RequestMapping(value = "/getMatchList")
    public ArrayList<TeamDTO> get_matchList() {
        ArrayList<TeamDTO> tList = tService.findAll();
        Comparator<TeamDTO> comparator = new Comparator<TeamDTO>() {
            @Override
            public int compare(TeamDTO a, TeamDTO b) {
                return b.getTTotal() - a.getTTotal();
            }
        };
        Collections.sort(tList, comparator);
        return tList;
    }

    @RequestMapping(value = "/getAgeList")
    public ArrayList<TeamDTO> get_ageList() {
        ArrayList<TeamDTO> tList = tService.findAll();
        Comparator<TeamDTO> comparator = new Comparator<TeamDTO>() {
            @Override
            public int compare(TeamDTO a, TeamDTO b) {
                int ageA = Integer.parseInt(a.getTAge().split("대 ")[0]);
                int ageB = Integer.parseInt(b.getTAge().split("대 ")[0]);
                if (ageA == ageB) {
                    String ageA2 = a.getTAge().split("대 ")[1];
                    String ageB2 = b.getTAge().split("대 ")[1];
                    if (ageA2.equals(ageB2)) {
                        return 0;
                    } else {
                        if (ageA2.equals("초반")) {
                            return ageB - ageA + 1;
                        }
                        if (ageA2.equals("후반")) {
                            return ageB - ageA - 1;
                        }
                    }
                }
                return ageB - ageA;
            }
        };
        Collections.sort(tList, comparator);
        return tList;
    }

    @RequestMapping(value = "/postReservation")
    public ArrayList<AppReservationDTO> post_reservation(@RequestParam("name") String nickName) {
        ArrayList<AppReservationDTO> rvList = new ArrayList<>();
        ReservationDTO dto = rvService.findByid(nickName);
        ArrayList<ReservationVO> rvoList = new ArrayList<>();
        try {
            rvoList = dto.getInfo();
        } catch (Exception e) {
            log.info("앱에서 내 예약 클릭시, 예약없음! 사용자 : " + nickName);
        }
        // 최신 예약으로 compare
        Comparator<ReservationVO> comparator = new Comparator<ReservationVO>() {
            @Override
            public int compare(ReservationVO a, ReservationVO b) {
                return b.getRegDate().compareTo(a.getRegDate());
            }
        };

        Collections.sort(rvoList, comparator);
        for (int i = 0; i < rvoList.size(); i++) {
            AppReservationDTO rv = new AppReservationDTO();
            rv.setFieldImg(fService.findByfName(rvoList.get(i).getField()).getFPic1());
            rv.setReservationDate(rvoList.get(i).getDate());
            rv.setRegDate(rvoList.get(i).getRegDate());
            rv.setTime(rvoList.get(i).getTime());
            rv.setFieldName(rvoList.get(i).getField());
            rv.setPrice(rvoList.get(i).getPrice());
            rv.setState(rvoList.get(i).getState());
            rv.setType(rvoList.get(i).getType());
            rv.setManner(rvoList.get(i).getManner());
            rv.setMannerornot(rvoList.get(i).getMannerornot());
            rvList.add(rv);
        }
        return rvList;
    }


    @RequestMapping(value = "/postReserveList", method = RequestMethod.POST)
    public ArrayList<ReserveListDTO> post_reserveList(@RequestParam("date") String date, @RequestParam("field") String field,
                                                      @RequestParam("time") String time) {
        ArrayList<ReserveListDTO> awayList = new ArrayList<>();
        ArrayList<TeamDTO> team = new ArrayList<>();
        try {
            awayList.addAll(rlService.findByFieldAndDateAndTime(field, date, time));
            for (int i = 0; i < awayList.size(); i++) {
                team.add(tService.findBytName(awayList.get(i).getTeam()));
                awayList.get(i).setTAge(team.get(i).getTAge());
                awayList.get(i).setTManner(team.get(i).getTManner());
                awayList.get(i).setTTotal(team.get(i).getTTotal());
                awayList.get(i).setLogoPath(team.get(i).getLogoPath());
                awayList.get(i).setUniform(team.get(i).getUniform());
            }
        } catch (Exception e) {
            return awayList;
        }
        return awayList;
    }


    @RequestMapping(value = "/postAcceptAway", method = RequestMethod.POST)
    public ArrayList<String> post_acceptAway(@RequestParam("awayName") String awayName, @RequestParam("date") String date, @RequestParam("field") String field,
                                             @RequestParam("time") String time, @RequestParam("id") String id) {

        ArrayList<String> tokenList = new ArrayList<>();

        ReservationDTO home = new ReservationDTO(); // 팀장별 사용기록
        ReservationDTO away = new ReservationDTO();
        ArrayList<ReservationVO> homeInfo = null;
        ArrayList<ReservationVO> awayInfo = null;
        ReserveDTO reserve = new ReserveDTO(); // 구장예약현황
        ArrayList<ReserveListDTO> reserveList = new ArrayList<>(); // 도전신청리스트
        MemberDTO memberHome = null; // 회원정보
        MemberDTO memberAway = null;
        String awayNickName = null;

        ZonedDateTime nowSeoul = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        String nowDate = nowSeoul.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        // reserve update
        // 홈팀의 구장명, 사용날짜, 사용시간과 어웨이팀이름으로 해당어웨이의 도전신청 조회
        reserveList = rlService.findByFieldAndDateAndTimeAndTeam(field, date, time, awayName);
        awayNickName = reserveList.get(0).getNickName();
        // 어웨이팀 정보를 reserve테이블에 추가 및 수정
        reserve = rService.findReserve(field, date, time);
        reserve.setNickNameB(awayNickName);
        reserve.setNameB(awayName);
        reserve.setDateB(nowDate);
        reserve.setState("B");

        // reservelist update
        // 선택된 어웨이팀의 리스트를 조회해서 같은날짜 같은시간 다른장소 신청제거
        rlService.deleteByDateAndTimeAndNickName(date, time, awayNickName);
        // 모든 사용자를 조회해서 같은날짜 같은시간 같은장소 신청자 제거(선택된 어웨이팀 포함)
        rlService.deleteByFieldAndDateAndTime(field, date, time);

        // reservation update : state "매칭 대기중" -> "매칭완료"
        home = rvService.findByid(reserve.getNickNameA());
        away = rvService.findByid(awayNickName);
        homeInfo = home.getInfo();
        awayInfo = away.getInfo();
        for (int i = 0; i < homeInfo.size(); i++) {
            String hField = homeInfo.get(i).getField();
            String hDate = homeInfo.get(i).getDate();
            String hTime = homeInfo.get(i).getTime();

            if (hField.equals(field) & hDate.equals(date) & hTime.equals(time)) {
                homeInfo.get(i).setState("매칭 완료");
            }
        }
        for (int i = 0; i < awayInfo.size(); i++) {
            String aField = awayInfo.get(i).getField();
            String aDate = awayInfo.get(i).getDate();
            String aTime = awayInfo.get(i).getTime();

            if (aField.equals(field) & aDate.equals(date) & aTime.equals(time)) {
                awayInfo.get(i).setState("매칭 완료");
            }
        }
        // member - hadPoint update
        memberHome = mService.findByid(id);
        memberAway = mService.findByNickName(awayNickName);

        memberHome.setHadPoint(memberHome.getHadPoint() + 50000);
        memberAway.setHadPoint(memberAway.getHadPoint() - 50000);
        mService.save(memberAway);

        // yuki start
        TeamDTO hometeam = tService.findBytName(memberHome.getTName());

        ArrayList<RecordVO> records = new ArrayList<RecordVO>();
        RecordVO record = new RecordVO();
        record.setField(field);
        record.setDate(date);
        record.setTime(time);
        record.setVsTeam(awayName);
        record.setResult(null);
        try{
            records = hometeam.getRecords();
            records.add(record);
        }catch (Exception e){
            records = new ArrayList<RecordVO>();
            records.add(record);
        }
        hometeam.setRecords(records);


        AlarmVO alarm = new AlarmVO();
        alarm.setSender(memberHome.getTName());
        alarm.setReceiver(awayName);
        alarm.setSendDate(nowDate);
        alarm.setIsRead("안읽음");
        alarm.setAlarmType("어웨이");
        alarm.setMessage(field + "\n" + date + " " + time + "경기\n홈팀 : " + memberHome.getTName());
        TeamDTO awayteam = tService.findBytName(awayName);

        for(int i=0;i<awayteam.getTeamMember().size();i++){
            MemberDTO awayteamMem = mService.findByNickName(awayteam.getTeamMember().get(i));
            ArrayList<AlarmVO> awayteamMemalarms = new ArrayList<>();
            try{
                awayteamMem.setAlarmCount(awayteamMem.getAlarmCount()+1);
                awayteamMemalarms = awayteamMem.getAlarms();
                awayteamMemalarms.add(alarm);
            }catch (Exception e){
                awayteamMemalarms = new ArrayList<>();
                awayteamMemalarms.add(alarm);
            }
            awayteamMem.setAlarms(awayteamMemalarms);
            mService.save(awayteamMem);
            Manner resManner = new Manner();
            resManner.setUnreadalarmcount(awayteamMem.getAlarmCount()-awayteamMem.getReadCount());
            resManner.setMessage(field + "\n" + date + " " + time + "경기\n홈팀 : " + memberHome.getTName());
            simpMessagingTemplate.convertAndSend("/queue/away/" + awayteamMem.getNickName(), resManner);
            log.info("홈팀이 어웨이팀 어셉트 했을때 어웨이팀에게 보내는 메세지 : " + resManner.getMessage());
            log.info("홈팀이 어웨이팀 어셉트 했을때 어웨이팀누구한테 보내냐면 : " + awayteamMem.getNickName());

            // Add Tokens
            if (awayteamMem.getFireBaseToken() != null) {
                tokenList.add(awayteamMem.getFireBaseToken());
                log.info("누구한테 가는 파베인가 : " + awayteamMem.getNickName());
            }
        }

        for(int i=0;i<hometeam.getTeamMember().size();i++){
            if(mService.findByNickName(hometeam.getTeamMember().get(i)).getNickName() != memberHome.getNickName()){
                AlarmVO alarm1 = new AlarmVO();
                alarm1.setSender(memberHome.getTName());
                alarm1.setReceiver(awayName);
                alarm1.setSendDate(nowDate);
                alarm1.setIsRead("안읽음");
                alarm1.setAlarmType("어웨이");
                alarm1.setMessage(field + "\n" + date + " " + time + "경기\n어웨이팀 : " + awayteam.getTName());
                MemberDTO hometeamMem = mService.findByNickName(hometeam.getTeamMember().get(i));
                ArrayList<AlarmVO> hometeamMemalarms = new ArrayList<AlarmVO>();
                try{
                    hometeamMem.setAlarmCount(hometeamMem.getAlarmCount()+1);
                    hometeamMemalarms = hometeamMem.getAlarms();
                    hometeamMemalarms.add(alarm1);
                }catch (Exception e){
                    hometeamMemalarms = new ArrayList<AlarmVO>();
                    hometeamMemalarms.add(alarm1);
                }
                hometeamMem.setAlarms(hometeamMemalarms);
                mService.save(hometeamMem);
                Manner resManner = new Manner();
                resManner.setUnreadalarmcount(hometeamMem.getAlarmCount()-hometeamMem.getReadCount());
                resManner.setMessage(field + "\n" + date + " " + time + "경기\n어웨이팀 : " + awayteam.getTName());
                simpMessagingTemplate.convertAndSend("/queue/away/" + hometeamMem.getNickName(), resManner);
                log.info("홈팀이 어웨이팀 어셉트 했을때 홈팀에게 보내는 메세지 : " + resManner.getMessage());
                log.info("홈팀이 어웨이팀 어셉트 했을때 홈팀누구한테 보내냐면 : " + hometeamMem.getNickName());

                // Add Tokens
                if(hometeamMem.getFireBaseToken() != null){
                    tokenList.add(hometeamMem.getFireBaseToken());
                }
            }
        }
        // 멤버홈인 놈한테 따로보내기
        AlarmVO alarm2 = new AlarmVO();
        alarm2.setSender(memberHome.getTName());
        alarm2.setReceiver(awayName);
        alarm2.setSendDate(nowDate);
        alarm2.setIsRead("안읽음");
        alarm2.setAlarmType("어웨이");
        alarm2.setMessage(field + "\n" + date + " " + time + "경기\n어웨이팀 : " + awayteam.getTName());
        ArrayList<AlarmVO> hometeamMemalarms = new ArrayList<AlarmVO>();
        try{
            memberHome.setAlarmCount(memberHome.getAlarmCount()+1);
            hometeamMemalarms = memberHome.getAlarms();
            hometeamMemalarms.add(alarm2);
        }catch (Exception e){
            hometeamMemalarms = new ArrayList<AlarmVO>();
            hometeamMemalarms.add(alarm2);
        }
        memberHome.setAlarms(hometeamMemalarms);
        mService.save(memberHome);
        Manner resManner = new Manner();
        resManner.setUnreadalarmcount(memberHome.getAlarmCount()-memberHome.getReadCount());
        resManner.setMessage(field + "\n" + date + " " + time + "경기\n어웨이팀 : " + awayteam.getTName());
        simpMessagingTemplate.convertAndSend("/queue/home/" + memberHome.getNickName(), resManner);
        log.info("홈팀이 어웨이팀 어셉트 했을때 홈팀에게 보내는 메세지 : " + resManner.getMessage());
        log.info("홈팀이 어웨이팀 어셉트 했을때 홈팀누구한테 보내냐면 : " + memberHome.getNickName());

        // Add Tokens
        if(memberHome.getFireBaseToken() != null){
            tokenList.add(memberHome.getFireBaseToken());
            log.info("누구한테 가는 파베인가 : " + memberHome.getNickName());
        }
        // yuki end
        Firebase firebase = new Firebase();
        // Try FireBase
        try {
            log.info("웹 -> 앱으로 보내는 홈팀의 어웨이팀 수락 알람 성공 : " + firebase.multipleSendToToken(tokenList, "매칭이 완료되었어요!", memberHome.getTName() + " vs " + memberAway.getTName() + "\n 홈 >> 예약에서 확인하기"));

        } catch (FirebaseMessagingException e){
            log.info("웹 -> 앱으로 보내는 홈팀의 어웨이팀 수락 알람 성공 : " + e.getMessage());
        }
        ArrayList<RecordVO> records2 = new ArrayList<RecordVO>();
        RecordVO record2 = new RecordVO();
        record2.setField(field);
        record2.setDate(date);
        record2.setTime(time);
        record2.setVsTeam(hometeam.getTName());
        record2.setResult(null);
        records2.add(record2);
        awayteam.setRecords(records2);
        tService.save(awayteam);

        // save
        tService.save(hometeam);
        rService.save(reserve);
        mService.save(memberHome);
        rvService.save(home);
        rvService.save(away);
        ArrayList<String> result = new ArrayList<>();
        result.add("done");
        return result;
    }
}