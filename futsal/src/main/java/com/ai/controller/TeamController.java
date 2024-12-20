package com.ai.controller;

import com.ai.domain.*;
import com.ai.service.*;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Controller
@Api(tags = {"팀관련 API"})
@RequestMapping(path = "/team")
public class TeamController {
    @Autowired
    TeamService service;
    @Autowired
    MemberService mService;
    @Autowired
    TeamService tService;
    @Autowired
    AmazonS3Client amazonS3Client;

    @Autowired
    ReserveService rService;

    @Autowired
    ReservationService rvService;

    @Autowired
    CommunityService cService;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ApiOperation(value = "팀 생성", response = TeamDTO.class)
    public ModelAndView insertTeam(@RequestParam("name") String tName, @RequestParam("location1") String location1,
                                   @RequestParam("location2") String location2,
                                   @RequestParam("birth") String birth, @RequestParam("uniform") String uniform,
                                   @RequestParam("total") int total, @RequestParam("age") String tAge,
                                   @RequestParam("manner") int tManner, @RequestParam("message") String TeamInfo, HttpSession session) {
        TeamDTO insertTeam = new TeamDTO();
        ModelAndView mav = new ModelAndView();
        String userId = (String) session.getAttribute("userId");
        String _id = mService.findByid(userId).getNickName();
        insertTeam.set_id(_id);
        insertTeam.setTName(tName);
        insertTeam.setTArea(location1 + " " + location2);
        insertTeam.setFoundingDate(birth);
        insertTeam.setUniform(uniform);
        insertTeam.setTTotal(total);
        insertTeam.setTAge(tAge);
        insertTeam.setTManner(tManner);
        insertTeam.setTeamInfo(TeamInfo);
        insertTeam.setLogoPath("https://fsmanager.s3.ap-northeast-2.amazonaws.com/image/logo.png");
        ArrayList<String> teammem = new ArrayList<String>();
        teammem.add(_id);
        insertTeam.setTeamMember(teammem);
        MemberDTO member = mService.findByid(userId);
        member.setTName(tName);
        mService.save(member);
        TeamDTO insertedTeam = service.insert(insertTeam);
        mav.addObject("insertedTeam", insertedTeam);
        mav.setViewName("redirect:/main");

        System.out.println("insert tName : " + insertedTeam.getTName());
        System.out.println("insert tArea : " + insertedTeam.getTArea());
        System.out.println("insert fDate : " + insertedTeam.getFoundingDate());
        System.out.println("insert tUniform : " + insertedTeam.getUniform());
        System.out.println("insert tTotal : " + insertedTeam.getTTotal());
        System.out.println("insert tAge : " + insertedTeam.getTAge());
        System.out.println("insert tManner : " + insertedTeam.getTManner());
        System.out.println("insert tInfo : " + insertedTeam.getTeamInfo());

        return mav;
    }

    @RequestMapping(value = "/teamInfo", method = RequestMethod.GET)
    @ApiOperation(value = "특정팀페이지로 이동", response = TeamDTO.class)
    public ModelAndView getTeamInfo(@RequestParam("tName") String tName, HttpSession session) {
        TeamDTO myteam = service.findBytName(tName);
        ModelAndView mav = new ModelAndView();
        TeamDTO team = null;
        MemberDTO member = mService.findByid((String) session.getAttribute("userId"));
        Integer point = null;
        ArrayList<String> applyMember = null;
        ArrayList<String> teamMember = null;
        // 최근전적가져오기 시작
        ReservationDTO reservation = null;
        ArrayList<ReservationVO> rvo = null;
        String field = null;
        String date = null;
        String time = null;
        ReserveDTO reservedto = null;
        ArrayList<ReserveDTO> reservelist = null;
        String iamteammate = "false";

        // 로그인한놈이팀멤버인지아닌지시작
        try {
            teamMember = myteam.getTeamMember(); //팀멤버가져오기
            team = tService.findBytName(member.getTName());
            applyMember = myteam.getApplyMember();
            point = member.getHadPoint();
            for (int i = 0; i < teamMember.size(); i++) {
                if (teamMember.contains(member.getNickName()) == true) {
                    iamteammate = "true";
                }
            }
            // 로그인한놈이팀멤버인지아닌지끝
            reservation = rvService.findByid(myteam.get_id()); // reservation중에 해당팀주장이름으로 된 reservationDTO가져오기
            try{
                rvo = reservation.getInfo(); // 해당 reservationDTO 안에 info 리스트 담기
            }catch (Exception e){
                rvo = new ArrayList<ReservationVO>();
            }
            reservelist = new ArrayList<ReserveDTO>();
            for (int i = 0; i < rvo.size(); i++) { // 해당 리스트크기만큼 반복문 돌리기
                if (rvo.get(i).getState().equals("매칭 완료")) { // 해당 리스트의 각 vo객체의 state가 매칭완료이놈
                    field = rvo.get(i).getField(); // 그 놈의 구장명
                    date = rvo.get(i).getDate(); // 그 놈의 날짜
                    time = rvo.get(i).getTime(); // 그 놈의 시간
                    //log.info("고놈의 구장명, 날짜, 시간 : " + field+" / "+date+" / "+time); // V
                    reservedto = rService.findReserve(field, date, time); // 고놈의 구장명,날짜,시간 이용해서 해당 예약찾아서 담기
                    //log.info("해당예약 : " + reservedto); // V
                    reservelist.add(reservedto); // addobject로 보낼 리스트에 담기
//                    log.info("해당예약담은 리스트 : " + reservelist); // V
                }
            }
//            log.info("최근전적 : " + reservelist);
            log.info("전적판수 : " + reservelist.size());
        } catch (Exception e) {
            log.info("최근전적가져오는trycatch문에러 : " + e.getMessage());
        }
        ArrayList<NoticeVO> notices = new ArrayList<NoticeVO>();
        try{
            notices = myteam.getNotices();
        }catch (Exception e){
            notices = null;
        }
        if(notices != null){
            ArrayList<NoticeVO> newnotices = new ArrayList<NoticeVO>();
            for(int i=notices.size()-1; i>=0; i--){
                newnotices.add(notices.get(i));
            }
            notices = newnotices;
        }
        Integer victory = 0;
        Integer draw = 0;
        Integer lose = 0;
        ArrayList<RecordVO> records = new ArrayList<>();
        try{
            records = myteam.getRecords();
            for(int i=0; i<myteam.getRecords().size();i++){
                RecordVO record = myteam.getRecords().get(i);
                if(record.getResult().equals("승")){
                    victory += 1;
                }else if(record.getResult().equals("무")){
                    draw += 1;
                }else if(record.getResult().equals("패")){
                    lose += 1;
                }
            }
        }catch (Exception e){
            log.info(myteam.getTName() + "팀의 경기결과기록이 없거나, 결과가 입력되지 않은 과거경기가 있습니다.");
//            records = new ArrayList<RecordVO>();
        }
        String myteamrecord = victory+"승 "+draw+"무 "+lose+"패";
        double manneravg = 0.0;
        if (myteam.getTMannercnt() != 0){
            manneravg = Double.parseDouble(String.format("%.1f", myteam.getTManner() / (double)myteam.getTMannercnt()));
            log.info("myteam.getTManner() : " + myteam.getTManner());
            log.info("myteam.getTMannercnt() : " +myteam.getTMannercnt());
        }
        log.info("manneravg : " + manneravg);

        ArrayList<ReserveDTO> reservelist1 = new ArrayList<>();
        if (reservelist.size() > 0){
            for(int i=reservelist.size()-1;i>=0;i--){
                reservelist1.add(reservelist.get(i)); // 마지막번호
            }
        }
        ArrayList<AlarmVO> readalarms = new ArrayList<AlarmVO>();
        ArrayList<AlarmVO> unreadalarms = new ArrayList<AlarmVO>();

        try{
            for(int i=member.getAlarms().size()-1;i>=0;i--){
                if(member.getAlarms().get(i).getIsRead().equals("읽음")){
                    readalarms.add(member.getAlarms().get(i));
                }
            }
        }catch (Exception e){
            log.info(e.getMessage());
            readalarms = null;
        }
        try{
            for(int i=member.getAlarms().size()-1;i>=0;i--){
                if(member.getAlarms().get(i).getIsRead().equals("안읽음")){
                    unreadalarms.add(member.getAlarms().get(i));
                }
            }
        }catch (Exception e){
            log.info(e.getMessage());
            unreadalarms = null;
        }
        String uuid = UUID.randomUUID().toString();
        mav.addObject("fNList", Utils.fNList);
        mav.addObject("latList", Utils.latList);
        mav.addObject("lonList", Utils.lonList);
        mav.addObject("uuid",uuid);
        mav.addObject("unreadalarms",unreadalarms);
        mav.addObject("readalarms",readalarms);
        mav.addObject("records", records);
        mav.addObject("manneravg", manneravg);
        mav.addObject("myteamrecord", myteamrecord);
        mav.addObject("notices", notices);
        mav.addObject("iamteammate", iamteammate); // 로그인한 회원이 해당 페이지의 팀의 팀원인지 아닌지
        mav.addObject("reservelist", reservelist1); // 해당 팀의 매칭완료된 예약만 가져온 리스트 -> 상대방도 꺼낼 수 있음
        mav.addObject("teamMember", teamMember);
        mav.addObject("applyMember", applyMember);
        mav.addObject("team", team);
        mav.addObject("point", point);
        mav.addObject("member", member);
        mav.addObject("myteam", myteam);
        mav.setViewName("/teamInfo");
        return mav;
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ApiOperation(value = "팀 검색", response = TeamDTO.class)
    @ResponseBody
    public ArrayList<TeamDTO> searchTeams(@RequestParam("searchTeam") String tName) throws IOException {
        log.info("searchController tName : " + tName);

        ArrayList<TeamDTO> Nteams = service.findByTNameRegex(tName);
        ArrayList<TeamDTO> Ateams = service.findByTAreaRegex(tName);
        ArrayList<TeamDTO> NAteams = new ArrayList<>(Nteams);
        NAteams.removeAll(Ateams);
        NAteams.addAll(Ateams);
        SearchMainDTO searchTeamtables = new SearchMainDTO();
        searchTeamtables.setNAteams(NAteams);

        System.out.println("========= SEARCHED TEAM NAME =========");
        for (int i = 0; i < NAteams.size(); i++) {
            System.out.println("검색 된 팀 이름 : " + NAteams.get(i).getTName());
        }

        return NAteams;
    }

    @RequestMapping(value = "/saveAsync", method = RequestMethod.POST)
    @ApiOperation(value = "팀 정보수정", response = TeamDTO.class)
    @ResponseBody
    public TeamDTO saveAsync(MultipartHttpServletRequest request, HttpSession session) throws Exception {
        MultipartFile uploadFile = request.getFile("uploadFile");
        String introduce = null;
        @SuppressWarnings("rawtypes") //rawtypes 썼다고 노란불뜨는 놈 제거해주는 역할 - @SuppressWarnings
        Enumeration e = request.getParameterNames();
        while (e.hasMoreElements()) {
            String introduceName = (String) e.nextElement();
            introduce = request.getParameter(introduceName);
        }
        log.info("introduce : " + introduce);
        String imageFilePath = "image";
        String imageFileName = UUID.randomUUID() + uploadFile.getOriginalFilename();
        log.info("imageFilePath : " + imageFilePath);
        log.info("imageFileName : " + imageFileName);
        log.info("로고 저장버튼 클릭.. upload..(이미지있음)");
        ObjectMetadata objectMetaData = new ObjectMetadata();
        objectMetaData.setContentType(uploadFile.getContentType());
        objectMetaData.setContentLength(uploadFile.getSize());
        String bucketPath = imageFilePath + "/" + imageFileName;
        amazonS3Client.putObject(
                new PutObjectRequest(bucket, bucketPath, uploadFile.getInputStream(), objectMetaData)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
        );
        String urlPath = amazonS3Client.getUrl(bucket, bucketPath).toString();
        log.info("urlPath : " + urlPath);
        ArrayList<String> urlPathList = new ArrayList<>();
        urlPathList.add(urlPath);
        TeamDTO dto = new TeamDTO();
        log.info("조회한 유저 정보 : " + mService.findByid((String) session.getAttribute("userId")));
        dto = service.findBytName(mService.findByid((String) session.getAttribute("userId")).getTName());
        dto.setLogoPath(urlPath);
        dto.setTeamInfo(introduce);
        log.info("수정된 팀의 소개 : " + dto.getTeamInfo());
        service.save(dto);
        return dto;
    }

    @RequestMapping(value = "/applyteamAjax", method = RequestMethod.POST)
    @ApiOperation(value = "팀 가입신청", response = TeamDTO.class)
    public void applyTeam(@RequestParam("tName") String tName, HttpSession session, HttpServletResponse response) throws Exception {
        PrintWriter applyout = response.getWriter();
        TeamDTO team = service.findBytName(tName);
        ModelAndView mav = new ModelAndView();
        MemberDTO member = mService.findByid((String) session.getAttribute("userId"));
        ArrayList<String> applymem = null;
        String result = null;
        try {
            applymem = team.getApplyMember();
            if (applymem.contains(member.getNickName()) == true) {
                applyout.print("fail");
            } else {
                applymem.add(member.getNickName());
                applyout.print("success");
            }
        } catch (Exception e) {
            log.info("해당 팀에 신청한명단이 없습니다.");
            applymem = new ArrayList<String>();
            applymem.add(member.getNickName());
            applyout.print("success");
        }
        team.setApplyMember(applymem);
        tService.save(team);
    }


    @RequestMapping(value = "/deleteApplyAjax", method = RequestMethod.POST)
    @ApiOperation(value = "팀 가입신청삭제", response = TeamDTO.class)
    public void deleteapply(@RequestParam("applytName") String applytName, HttpSession session, HttpServletResponse response) throws Exception {
        log.info("deleteApplyAjax 컨트롤러 들어옴,,,");
        PrintWriter deleteout = response.getWriter();
        TeamDTO applyteam = service.findBytName(applytName);
        ModelAndView mav = new ModelAndView();
        MemberDTO member = mService.findByid((String) session.getAttribute("userId"));
        ArrayList<String> applymem = null;
        try {
            applymem = applyteam.getApplyMember();
            for (int i = 0; i < applymem.size(); i++) {
                if (applymem.get(i).equals(member.getNickName())) {
                    applymem.remove(i);
                    applyteam.setApplyMember(applymem);
                    tService.save(applyteam);
                    deleteout.print("success");
                }
            }
        } catch (Exception e) {
            log.info("해당팀은 가입신청한 인간이 없습니다.");
            deleteout.print("fail");
        }
        log.info("deleteout : " + deleteout);
    }

    @RequestMapping(value = "/acceptApplyAjax", method = RequestMethod.POST)
    @ApiOperation(value = "팀 가입신청수락", response = TeamDTO.class)
    public void acceptapply(@RequestParam("applymem") String applymem, HttpSession session, HttpServletResponse response) throws Exception {
        log.info("acceptApplyAjax 컨트롤러 들어옴,,,");
        PrintWriter acceptout = response.getWriter();
        MemberDTO applymember = mService.findByNickName(applymem);
        MemberDTO member = mService.findByid((String) session.getAttribute("userId"));
        TeamDTO thisteam = tService.findBytName(member.getTName());
        ArrayList<CommunityDTO> cdto = new ArrayList<>();
        ArrayList<String> teammembers = null;
        // 해당팀의 신청리스트에서 승인받은놈삭제
        for (int i = 0; i < thisteam.getApplyMember().size(); i++) {
            if (thisteam.getApplyMember().get(i).equals(applymem) == true) {
                thisteam.getApplyMember().remove(i);
            }
        }
        // 해당팀의 멤버에 승인받은놈 넣기
        try {
            teammembers = thisteam.getTeamMember();
            teammembers.add(applymem);
        } catch (Exception e) {
            log.info("해당팀의 팀원이 아직 없다");
            teammembers = new ArrayList<String>();
            teammembers.add(applymem);
        }
        thisteam.setTeamMember(teammembers);

        // 모든 팀의신청리스트에서 승인받은놈 이름 삭제
        try {
            cdto = cService.findByWriter(applymember.getNickName());
            for (int i = 0; i < cdto.size(); i++) {
                cdto.get(i).setTName(thisteam.getTName());
                cdto.get(i).setUniform(thisteam.getUniform());
                cService.save(cdto.get(i));
            }
            ArrayList<TeamDTO> allteam = tService.findAll(); //모든팀불러와서 담기
            for (int i = 0; i < allteam.size(); i++) { //모든팀갯수만큼 돌리기
                for (int j = 0; j < allteam.get(i).getApplyMember().size(); j++) { // 각팀의 가입신청사람수 만큼 돌리기
                    if (allteam.get(i).getApplyMember().get(j).equals(applymem)) { // 가입신청사람이름이랑 승인받은놈이름이랑같으면

                        allteam.get(i).getApplyMember().remove(j); // 승인받은놈을 가입신청리스트에서 삭제
                        tService.save(allteam.get(i)); // 모든팀을 인덱스순으로 하나씩 모두 세이브
                    }
                }
            }
        } catch (Exception e) {
            log.info("반복문 돌리는 중에 가입신청리스트가 존재하지 않는 팀일때");
        }

        // 해당 회원 멤버디비에 팀네임 삽입
        try {
            applymember.setTName(member.getTName());
            mService.save(applymember);
        } catch (Exception e) {
            log.info("팀이 없음");
        }
        acceptout.print("success");
        tService.save(thisteam);
    }

    @RequestMapping(value = "/rejectApplyAjax", method = RequestMethod.POST)
    @ApiOperation(value = "팀 가입신청거부", response = TeamDTO.class)
    public void rejectapply(@RequestParam("rejectmem") String rejectmem, HttpSession session, HttpServletResponse response) throws Exception {
        log.info("rejectApplyAjax 컨트롤러 들어옴,,,");
        PrintWriter rejecttout = response.getWriter();
        MemberDTO member = mService.findByid((String) session.getAttribute("userId"));
        TeamDTO thisteam = tService.findBytName(member.getTName());
        ArrayList<String> teammembers = null;
        // 해당팀의 신청리스트에서 승인받은놈삭제
        for (int i = 0; i < thisteam.getApplyMember().size(); i++) {
            if (thisteam.getApplyMember().get(i).equals(rejectmem) == true) {
                thisteam.getApplyMember().remove(i);
            }
        }
        tService.save(thisteam);

        rejecttout.print("success");
        tService.save(thisteam);
    }

    @RequestMapping(value = "/teamInfo", method = RequestMethod.POST)
    @ResponseBody
    public TeamDTO postTeamInfo(@RequestParam("tName") String tName) {
        return service.findBytName(tName);
    }

    @RequestMapping(value = "/kickMemberAjax", method = RequestMethod.POST)
    @ApiOperation(value = "팀원 추방", response = TeamDTO.class)
    public void kickMember(@RequestParam("kickmem") String kickmem, HttpSession session, HttpServletResponse response) throws Exception {
        log.info("kickMemberAjax 컨트롤러 들어옴,,,");
        PrintWriter kickmemout = response.getWriter();
        MemberDTO kickMember = mService.findByNickName(kickmem);
        ArrayList<CommunityDTO> cdto = new ArrayList<>();
        MemberDTO member = mService.findByid((String) session.getAttribute("userId"));
        TeamDTO thisteam = tService.findBytName(member.getTName());
        ArrayList<String> teammembers = null;

        // 해당팀의 팀원리스트에서 kickmem삭제
        for (int i = 0; i < thisteam.getTeamMember().size(); i++) {
            if (thisteam.getTeamMember().get(i).equals(kickmem) == true) {
                thisteam.getTeamMember().remove(i);
            }
        }
        cdto = cService.findByWriter(kickMember.getNickName());
        for (int i = 0; i < cdto.size(); i++) {
            cdto.get(i).setTName("");
            cdto.get(i).setUniform("");
            cService.save(cdto.get(i));
        }
        tService.save(thisteam);
        kickmemout.print("success");

        // 해당 멤버의 멤버db에서 tName 삭제
        kickMember.setTName(null);
        mService.save(kickMember);
    }

    @RequestMapping(value = "/exitTeamAjax", method = RequestMethod.POST)
    @ApiOperation(value = "팀 탈퇴", response = TeamDTO.class)
    public void exitTeam(@RequestParam("exitteam") String exitteam, HttpSession session, HttpServletResponse response) throws Exception {
        log.info("exitTeamAjax 컨트롤러 들어옴,,,");
        PrintWriter exitteamout = response.getWriter();
        ArrayList<CommunityDTO> cdto = new ArrayList<>();
        TeamDTO exitTeamdto = tService.findBytName(exitteam);
        MemberDTO savemem = mService.findByid((String) session.getAttribute("userId"));
        for (int i = 0; i < exitTeamdto.getTeamMember().size(); i++) {
            if (exitTeamdto.getTeamMember().get(i).equals(savemem.getNickName())) {
                exitTeamdto.getTeamMember().remove(i);
                tService.save(exitTeamdto);
            }
        }
        cdto = cService.findByWriter(savemem.getNickName());
        for (int i = 0; i < cdto.size(); i++) {
            cdto.get(i).setTName("");
            cdto.get(i).setUniform("");
            cService.save(cdto.get(i));
        }

        savemem.setTName(null);
        mService.save(savemem);
        exitteamout.print("success");
    }
    @RequestMapping(value="/submitnotice", method = RequestMethod.POST)
    @ApiOperation(value = "팀 공지등록", response = TeamDTO.class)
    @ResponseBody
    public NoticeVO submitnotice(@RequestParam("tname") String tName, @RequestParam("notice") String notice, HttpSession session)throws Exception{
        log.info("submitnotice 컨트롤러 들어옴 .,,,,,, 받는 파라미터 확인 : " + tName + " // " + notice);
        // 여기서부터 작업시작 - > 공지를 팀 DB에 넣고 teamInfo.html에 보여주기
        TeamDTO myteam = tService.findBytName(tName);
        ZonedDateTime nowSeoul = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        String parsedcurDateTime = nowSeoul.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); //현재시간을 String으로 파씽
        ArrayList<NoticeVO> noticevoList = new ArrayList<NoticeVO>();
        NoticeVO noticevo = new NoticeVO();
        noticevo.setTName(tName);
        noticevo.setNotice(notice);
        noticevo.setRegDate(parsedcurDateTime);
        try{
            noticevoList = myteam.getNotices();
            noticevoList.add(noticevo);
        }catch (Exception e){
            noticevoList = new ArrayList<NoticeVO>();
            noticevoList.add(noticevo);
        }
        log.info("noticevoList 중간 점검 : " + noticevoList);
        myteam.setNotices(noticevoList);
        tService.save(myteam);

        return noticevo;
    }

    @RequestMapping(value = "/duplicatetName", method = RequestMethod.POST)
    @ApiOperation(value = "팀 생성시 중복검사", response = TeamDTO.class)
    @ResponseBody
    public String duplicatetName(@RequestParam("tName")String tName){
        log.info("duplicatetName 컨트롤러 들어옴,,,,, 받은 값 : " + tName);
        String result = "";
        ArrayList<TeamDTO> allteam = tService.findAll();
        ArrayList<String> allteamname = new ArrayList<>();
        for (int i=0; i<allteam.size();i++){
            allteamname.add(allteam.get(i).getTName());
        }
        if (allteamname.contains(tName)){
            result = "중복";
        }else {
            result = "사용가능";
        }
        log.info("패치로 다시 보낼 값 : " + result);
        return result;
    }

    @RequestMapping(value = "/sortTotal", method = RequestMethod.POST)
    @ApiOperation(value = "팀 전적정렬", response = TeamDTO.class)
    @ResponseBody
    public ArrayList<TeamDTO> sortteamtotal(@RequestParam("total1")Integer total1, @RequestParam("total2")Integer total2){
        log.info("sortteamtotal 컨트롤러 틀어옴,,, 받은 값 : " + total1 + " / " + total2);
        TeamDTO team = new TeamDTO();
        ArrayList<TeamDTO> allteams = tService.findAll();
        ArrayList<TeamDTO> allteams2 = new ArrayList<>();
        for(int i=0;i<allteams.size();i++){
            log.info(""+allteams.get(i).getTTotal());
        }

        if(total1 > total2){
            Collections.sort(allteams, new TeamComparator());
        }else if(total1 < total2){
            Collections.sort(allteams, new TeamComparator());
            for(int i=allteams.size()-1;i>=0;i--){
                TeamDTO team2= allteams.get(i);
                allteams2.add(team2);
            }
            allteams = allteams2;
        }
        for(int i=0;i<allteams.size();i++){
            log.info(""+allteams.get(i).getTTotal());
        }
        return allteams;
    }
}