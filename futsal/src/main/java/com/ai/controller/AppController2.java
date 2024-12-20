package com.ai.controller;

import com.ai.domain.*;
import com.ai.service.*;
import com.google.firebase.messaging.FirebaseMessagingException;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

@Slf4j
@RestController
@Api(tags = {"모바일 앱 관련 2/2 API"})
@RequestMapping(value = "/app")
public class AppController2 {
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

    @RequestMapping(value = "/getBoard", method = RequestMethod.GET)
    public AppSearchBoardDTO get_community_board() {
        AppSearchBoardDTO res = new AppSearchBoardDTO();
        ArrayList<CommunityDTO> allList = cService.findAll();
        ArrayList<CommunityDTO> reserveList = new ArrayList<>();
        for(CommunityDTO item : allList){
            if(!item.getRsvFld().equals("예약하신 구장이 없습니다.")){
                reserveList.add(item);
            }
        }
        Collections.reverse(allList);
        Collections.reverse(reserveList);
        res.setTitleSort(allList);
        res.setTeamSort(reserveList);
        log.info("앱에서 게스트가 커뮤니티 확인");
        return res;
    }

    @RequestMapping(value = "/postBoard", method = RequestMethod.POST)
    public AppSearchBoardDTO post_community_board(@RequestParam("nickName")String nickName) {

        AppSearchBoardDTO res = new AppSearchBoardDTO();
        ArrayList<CommunityDTO> allList = cService.findAll();
        ArrayList<CommunityDTO> reserveList = new ArrayList<>();
        for(CommunityDTO item : allList){
            if(!item.getRsvFld().equals("예약하신 구장이 없습니다.")){
                reserveList.add(item);
            }
        }
        Collections.reverse(allList);
        Collections.reverse(reserveList);
        // 차단 제외
        ArrayList<String> blockList = mService.findByNickName(nickName).getBlockList();
        log.info("앱에서 커뮤니티 진입 시 차단 리스트 확인중.. 닉네임 : " + nickName);
        try {
            for(int i = 0; i < blockList.size(); i++){
                for(int j = 0; j < allList.size(); j++){
                    if(blockList.get(i).equals(allList.get(j).getWriter())){
                        allList.remove(j);
                    }
                }
                for(int k = 0; k < reserveList.size(); k++){
                    if(blockList.get(i).equals(reserveList.get(k).getWriter())){
                        reserveList.remove(k);
                    }
                }
            }
        } catch (Exception e){
            log.info("해당 유저의 차단 리스트 없음");
        }
        log.info("앱에서 커뮤니티 진입 시 신고누적 확인중..");
        // 신고 누적 제외 (3회 이상 기준)
        for(int i = 0; i < allList.size(); i++){
            if(allList.get(i).getReportNickNames().size() >= Utils.reportCount){
                allList.remove(i);
            }
        }
        for(int i = 0; i < reserveList.size(); i++){
            if(reserveList.get(i).getReportNickNames().size() >= Utils.reportCount){
                reserveList.remove(i);
            }
        }

        res.setTitleSort(allList);
        res.setTeamSort(reserveList);

        return res;
    }

    @RequestMapping(value = "/postBoardOne", method = RequestMethod.POST)
    public AppCommunityDTO post_community_board_one(@RequestParam("board_idx") String board_idx, @RequestParam("id") String userId) {
        AppCommunityDTO dto = new AppCommunityDTO();
        MemberDTO member = new MemberDTO();
        CommunityDTO community = cService.findByIdx(Integer.valueOf(board_idx));
        try {
            member = mService.findByid(userId);
            log.info("앱에서 게시글을 클릭한 유저가 회원임 : " + member.getNickName());
            if (!community.getWriter().equals(member.getNickName())) {
                community.setViews(community.getViews() + 1);
                cService.save(community);
            }
        } catch (Exception e){
            log.info("앱에서 게시글을 클릭한 유저가 게스트임");
            community.setViews(community.getViews() + 1);
            cService.save(community);
        }
        TeamDTO myTeam = tService.findBytName(community.getTName());
        Integer fldPrc = -1;
        String[] rsvFld = community.getRsvFld().split(",");
        if (!(rsvFld[0].contains("없습니다.") | rsvFld[0].contains("선택"))) {
            fldPrc = fService.findByfName(rsvFld[0]).getFPrice1();
        }
        dto.setRsvFld(rsvFld);
        dto.setFldPrc(fldPrc);
        dto.setTeam(myTeam);
        dto.setCommunity(community);
        return dto;
    }

    @RequestMapping(value = "/postWrite", method = RequestMethod.POST)
    public ArrayList<ReservationVO> post_write(@RequestParam("nickName") String nickName) {
        String writer = nickName;
        ArrayList<ReservationVO> new_reser = new ArrayList<>();
        ArrayList<ReservationVO> reservation;
        try {
            reservation = rvService.findByid(writer).getInfo();
            int _size = reservation.size();
            for (int i = 0; i < _size; i++) {
                if (reservation.get(i).getState().equals("매칭 대기중") && reservation.get(i).getType().equals("홈")) {
                    new_reser.add(reservation.get(i));
                }
            }
        } catch (Exception e) {
            new_reser.add(new ReservationVO());
        }
        return new_reser;
    }

    @RequestMapping(value = "/postDoWrite", method = RequestMethod.POST)
    public ArrayList<String> post_do_write(@RequestParam("writer") String writer, @RequestParam("title") String title, @RequestParam("message") String message, @RequestParam("rsvFld") String rsvFld) {
        ArrayList<String> result = new ArrayList<>();
        CommunityDTO community = new CommunityDTO();
        community.setIdx(sService.generateSequence(CommunityDTO.SEQUENCE_NAME));
        community.setTitle(title);
        community.setWriter(writer);
        community.setMessage(message);
        try {
            String tName = mService.findByNickName(writer).getTName();
            community.setTName(tName);
            log.info("글 작성자의 팀 : " + tName);
            TeamDTO team = tService.findBytName(tName);
            String uniform = team.getUniform();
            log.info("글 작성자의 팀의 유니폼 : " + uniform);
            community.setUniform(uniform);
        } catch (Exception e) {
            log.info("글 작성자 : " + writer + ", 해당하는 사람의 team정보 없음");
            community.setTName("");
            community.setUniform("");
        }
        String regDate = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        community.setRegDate(regDate);
        community.setRsvFld(rsvFld);
        community.setViews(0);
        community.setReply(new ArrayList<CommunityVO>());
        community.setReportNickNames(new ArrayList<String>());
        community.setReportMessages(new ArrayList<String>());
        cService.insert(community);
        result.add("done");
        return result;
    }

    @RequestMapping(value = "/postComment", method = RequestMethod.POST)
    public ArrayList<CommunityVO> post_comment(@RequestParam("bNo") String bNo, @RequestParam("nickName") String nickName, @RequestParam("message") String message) {
        CommunityDTO dto = cService.findByIdx(Integer.valueOf(bNo));
        ArrayList<CommunityVO> voList = dto.getReply();
        CommunityVO vo = new CommunityVO();
        vo.setIdx(sService.generateSequence(vo.SEQUENCE_NAME));
        vo.setMessage(message);
        vo.setWriter(nickName);
        voList.add(vo);
        dto.setReply(voList);
        cService.save(dto);
        log.info("앱에서 댓글 생성");
        return voList;
    }

    // 게시글에서 해당 글의 예약 정보가 매칭 대기중인지 판별 여부
    @RequestMapping(value = "/postCheckBoard", method = RequestMethod.POST)
    public ArrayList<String> post_check_board(@RequestParam("field") String field, @RequestParam("date") String date, @RequestParam("time") String time) {
        ArrayList<String> result = new ArrayList<>();
        ArrayList<ReservationDTO> dtoList = rvService.findAll();
        for (int i = 0; i < dtoList.size(); i++) {
            for (int j = 0; j < dtoList.get(i).getInfo().size(); j++) {
                if (dtoList.get(i).getInfo().get(j).getField().equals(field) && dtoList.get(i).getInfo().get(j).getDate().equals(date) && dtoList.get(i).getInfo().get(j).getTime().equals(time)) {
                    result.add(dtoList.get(i).getInfo().get(j).getState());
                }
            }
        }
        log.info("앱에서 게시글 예약 상태 확인");
        return result;
    }

    // 매칭 대기중일때 해당 예약 대상자가 나인지 파악 && 해당 예약 팀이 내팀인지 파악 후 예약진행
    @RequestMapping(value = "/postBoardReserve", method = RequestMethod.POST)
    public ArrayList<String> post_board_reserve(@RequestParam("field") String field, @RequestParam("date") String date, @RequestParam("time") String time, @RequestParam("price") String price, @RequestParam("nickName") String nickName, @RequestParam("team") String team) {
        ArrayList<String> result = new ArrayList<>();
        price = price.replaceAll("\\B(?=(\\d{3})+(?!\\d))", ",") + " P";
        ReserveDTO reserve = rService.findReserve(field, date, time);
        String regDate = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if (reserve.getNameA().equals(team) || reserve.getNickNameA().equals(nickName)) {
            result.add("홈 & 어웨이로 변경하시겠습니까?");
        } else {
            ArrayList<ReserveListDTO> checkList = rlService.findByFieldAndDateAndTime(field, date, time);
            if (checkList.size() > 0) {
                result.add("중복 예약");
            } else {
                ReserveListDTO rl = new ReserveListDTO();
                rl.setField(field);
                rl.setDate(date);
                rl.setTime(time);
                rl.setTeam(team);
                rl.setPrice(price);
                rl.setNickName(nickName);
                rl.setRegDate(regDate);
                rlService.insert(rl);

                ReservationDTO rv = rvService.findByid(nickName);
                ArrayList<ReservationVO> rvoList = rv.getInfo();
                ReservationVO rvo = new ReservationVO();
                rvo.setDate(date);
                rvo.setRegDate(regDate);
                rvo.setTime(time);
                rvo.setField(field);
                rvo.setPrice(price);
                rvo.setState("매칭 대기중");
                rvo.setType("어웨이");
                rvo.setFieldPic(fService.findByfName(field).getFPic1());
                try {
                    rvoList = rv.getInfo();
                    rvoList.add(rvo);
                    rv.setInfo(rvoList);
                    rvService.save(rv);
                } catch (Exception e) {
                    rvoList = new ArrayList<ReservationVO>();
                    rvoList.add(rvo);
                    rv.setInfo(rvoList);
                    ReservationDTO rv2 = new ReservationDTO();
                    rv2.setId(nickName);
                    rv2.setInfo(rvoList);
                    rvService.insert(rv2);
                }
                result.add("done");
                // yuki start
                // 어웨이팀(tName)가 도전할때, 홈팀(dto.getNameA())한테 알람보내기
                TeamDTO hometeam = tService.findBytName(reserve.getNameA());
                MemberDTO hometeamreservationmember = mService.findByNickName(reserve.getNickNameA());
                AlarmVO alarm = new AlarmVO();
                alarm.setSender(team);
                alarm.setReceiver(reserve.getNameA());
                alarm.setSendDate(regDate);
                alarm.setIsRead("안읽음");
                alarm.setAlarmType("홈");
                alarm.setMessage(field + "\n" + date + " " + time + "경기\n도전팀 : " + team);
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
                resManner.setMessage(field + "\n" + date + " " + time + "경기\n도전팀 : " + team);
                simpMessagingTemplate.convertAndSend("/queue/home/" + hometeamreservationmember.getNickName(), resManner);
                log.info("어웨이팀이 홈팀에게 도전 했을때 보내는 메세지 : " + resManner.getMessage());
                // yuki end


                // FireBase
                Firebase firebase = new Firebase();
                if (hometeamreservationmember.getFireBaseToken() != null) {
                    try {
                        log.info("앱에 게시판에서 어웨이 팀 도전 알림 성공 : " + firebase.sendToToken(hometeamreservationmember.getFireBaseToken(), "도전 신청이 도착했어요", "홈 >> 예약에서 확인하기"));
                    } catch (FirebaseMessagingException e) {
                        log.info("앱에 게시판에서 어웨이 팀 도전 알림 실패 : " + e.getMessage());
                    }
                }
            }
        }
        log.info("앱에서 매칭 대기중인 것을 확인");
        return result;
    }

    // 게시글에서 홈 & 어웨이로 바꾸겠다는 사용자
    @RequestMapping(value = "/postBoardChangeReserve", method = RequestMethod.POST)
    public ArrayList<String> post_board_change_reserve(@RequestParam("field") String fName, @RequestParam("date") String date, @RequestParam("time") String time, @RequestParam("price") String price, @RequestParam("nickName") String nickName) {
        ArrayList<String> result = new ArrayList<>();
        price = price.replaceAll("\\B(?=(\\d{3})+(?!\\d))", ",") + " P";
        boolean isSaved = false;
        ReservationDTO rv = rvService.findByid(nickName);
        ReservationVO rvo = new ReservationVO();
        ReserveDTO dto = rService.findReserve(fName, date, time);
        ZonedDateTime nowSeoul = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        String nowDate = nowSeoul.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        for (int i = 0; i < rv.getInfo().size(); i++) {
            if (rv.getInfo().get(i).getDate().equals(date) && rv.getInfo().get(i).getTime().equals(time) && rv.getInfo().get(i).getField().equals(fName)) {
                rvo = rv.getInfo().get(i);
                rvo.setState("매칭 완료");
                rvo.setType("홈 & 어웨이");
                rv.getInfo().set(i, rvo);
                rvService.save(rv);
                isSaved = true;
            }
        }
        if (isSaved == false) {
            ArrayList<ReservationVO> rvoList = rv.getInfo();
            rvo = new ReservationVO();
            rvo.setDate(date);
            rvo.setRegDate(nowDate);
            rvo.setTime(time);
            rvo.setField(fName);
            rvo.setPrice(price);
            rvo.setState("매칭 완료");
            rvo.setType("홈 & 어웨이");
            rvoList.add(rvo);
            rv.setInfo(rvoList);
            rvService.save(rv);
        }

        dto.setNickNameB(nickName);
        dto.setNameB(mService.findByNickName(nickName).getTName());
        dto.setDateB(nowDate);
        dto.setType("all");
        dto.setState("B");
        rService.save(dto);

        rvo = new ReservationVO();
        rv = rvService.findByid(dto.getNickNameA());
        for (int i = 0; i < rv.getInfo().size(); i++) {
            if (rv.getInfo().get(i).getDate().equals(date) && rv.getInfo().get(i).getTime().equals(time) && rv.getInfo().get(i).getField().equals(fName)) {
                rvo = rv.getInfo().get(i);
                rvo.setState("매칭 완료");
                rvo.setType("홈 & 어웨이");
                rv.getInfo().set(i, rvo);
                rvService.save(rv);
            }
        }

        result.add("done");
        log.info("게시글에서 홈 & 어웨이로 예약이 변경됨");
        return result;
    }

    @RequestMapping(value = "/postAutoLogin", method = RequestMethod.POST)
    public MemberDTO post_auto_login(@RequestParam("nickName") String nickName, @RequestParam("fireBaseToken") String fireBaseToken) {
        MemberDTO dto = new MemberDTO();
        try {
            dto = mService.findByNickName(nickName);
            dto.setFireBaseToken(fireBaseToken);
            mService.save(dto);
            log.info(dto.getNickName() + " 회원이 로그인");
        } catch (Exception e) {
            log.info("앱 자동 로그인 실패! 사유 : userInfo 없음");
        }
        return dto;
    }

    @RequestMapping(value = "/postSearchBoard", method = RequestMethod.POST)
    public AppSearchBoardDTO post_search_board(@RequestParam("searchText") String searchText) {
        AppSearchBoardDTO res = new AppSearchBoardDTO();
        ArrayList<CommunityDTO> itemList = cService.findAll();
        ArrayList<CommunityDTO> titleSort = new ArrayList<>();
        ArrayList<CommunityDTO> teamSort = new ArrayList<>();
        ArrayList<CommunityDTO> writerSort = new ArrayList<>();
        ArrayList<CommunityDTO> reserveSort = new ArrayList<>();
        // title
        for (CommunityDTO item : itemList) {
            if (item.getTitle().contains(searchText)) {
                titleSort.add(item);
            }
        }
        // team
        for (CommunityDTO item : itemList) {
            if (item.getTName().contains(searchText)) {
                teamSort.add(item);
            }
        }
        // writer
        for (CommunityDTO item : itemList) {
            if (item.getWriter().contains(searchText)) {
                writerSort.add(item);
            }
        }
        // reserve
        for (CommunityDTO item : itemList) {
            if (item.getRsvFld().contains(searchText) && (!item.getRsvFld().equals("예약하신 구장이 없습니다."))) {
                reserveSort.add(item);
            }
        }

        log.info("앱에서 게시글 검색 텍스트 : " + searchText + " 의 결과 보냄");
        Collections.reverse(titleSort);
        Collections.reverse(teamSort);
        Collections.reverse(writerSort);
        Collections.reverse(reserveSort);
        res.setTitleSort(titleSort);
        res.setTeamSort(teamSort);
        res.setWriterSort(writerSort);
        res.setReserveSort(reserveSort);
        return res;
    }

    @RequestMapping(value = "/postClickNotification", method = RequestMethod.POST)
    public MemberDTO post_click_notification(@RequestParam("nickName")String nickName, @RequestParam("alramNo") Integer alramNo){
        MemberDTO result = mService.findByNickName(nickName);
        ArrayList<AlarmVO> alarms = result.getAlarms();
        AlarmVO alarm = alarms.get(alramNo);
        alarm.setIsRead("읽음");
        alarms.set(alramNo, alarm);
        Comparator<AlarmVO> comparator = new Comparator<AlarmVO>() {
            @Override
            public int compare(AlarmVO a, AlarmVO b) {
                int countA = 1;
                int countB = 1;
                if(a.getIsRead().equals("안읽음")){
                    countA = 0;
                }
                if(b.getIsRead().equals("안읽음")){
                    countB = 0;
                }
                return countA - countB;
            }
        };

        Collections.sort(alarms, comparator);
        result.setAlarms(alarms);
        result.setReadCount(result.getReadCount() + 1);
        mService.save(result);
        log.info("정상적으로 알림 횟수 처리됨");
        return result;
    }


    @RequestMapping(value = "/postCreateTeam", method = RequestMethod.POST)
    public MemberDTO post_create_team(@RequestParam("tName") String tName, @RequestParam("location1") String location1,
                                      @RequestParam("location2") String location2,
                                      @RequestParam("birth") String birth, @RequestParam("uniform") String uniform, @RequestParam("age") String tAge,
                                      @RequestParam("message") String TeamInfo, @RequestParam("captain") String captain) {
        TeamDTO insertTeam = new TeamDTO();
        MemberDTO member = new MemberDTO();
        try {
            log.info("앱에서 팀 생성시도 했으나 이미 존재하는 팀! 팀 명 : " + tName);
            TeamDTO checkTeam = tService.findBytName(tName);
            log.info("이미으로 존재하는 팀의 팀장 : " + checkTeam.get_id());
        } catch (Exception e){
            insertTeam.set_id(captain);
            insertTeam.setTName(tName);
            insertTeam.setTArea(location1 + " " + location2);
            insertTeam.setFoundingDate(birth);
            insertTeam.setUniform(uniform);
            insertTeam.setTTotal(0);
            insertTeam.setTAge(tAge);
            insertTeam.setTManner(0);
            insertTeam.setTeamInfo(TeamInfo);
            insertTeam.setLogoPath("https://fsmanager.s3.ap-northeast-2.amazonaws.com/image/logo.png");
            ArrayList<String> teammem = new ArrayList<String>();
            teammem.add(captain);
            insertTeam.setTeamMember(teammem);
            member = mService.findByNickName(captain);
            member.setTName(tName);
            mService.save(member);
            TeamDTO insertedTeam = tService.insert(insertTeam);
            log.info("앱에서 팀 생성됨! 팀 명 : " + tName + ", 팀장 : " +captain);
        }

        return member;
    }

    @RequestMapping(value = "/postMyInfo", method = RequestMethod.POST)
    public ArrayList<Integer> post_my_info(@RequestParam("nickName")String nickName){
        ArrayList<Integer> result = new ArrayList<>();
        Integer user_reservations = 0;
        Integer user_deposits = 0;
        Integer user_boards = 0;
        Integer user_comments = 0;

        try {
            ReservationDTO rv = rvService.findByid(nickName);
            if (rv.getInfo().size() != 0){
                user_reservations = rv.getInfo().size();
            }
        } catch (Exception e){
            log.info("앱에서 내 정보 보기 >> " + nickName + "님의 예약 정보 없음!");
        }

        try {
            ArrayList<CommunityDTO> com = cService.findByWriter(nickName);
            if(com.size() != 0){
                user_boards = com.size();
            }
        } catch (Exception e){
            log.info("앱에서 내 정보 보기 >> " + nickName + "님의 게시글 내역 없음!");
        }

        try {
            ArrayList<CommunityDTO> boardList = cService.findAll();
            for(CommunityDTO board : boardList){
                ArrayList<CommunityVO> commentList = board.getReply();
                for(CommunityVO comment : commentList){
                    if(comment.getWriter().equals(nickName)){
                        user_comments ++;
                    }
                }
            }
        } catch (Exception e){

        }
        if(user_comments == 0){
            log.info("앱에서 내 정보 보기 >> " + nickName + "님의 댓글 내역 없음!");
        }
        result.add(user_reservations);
        result.add(user_deposits);
        result.add(user_boards);
        result.add(user_comments);
        return result;
    }

    @RequestMapping(value = "/postInsertManner", method = RequestMethod.POST)
    @ResponseBody
    public ArrayList<String> post_insert_manner(@RequestParam("field") String field, @RequestParam("date") String date, @RequestParam("time") String time, @RequestParam("myteam") String myteam,
                                                @RequestParam("mannerscore") Integer mannerscore, @RequestParam("matchresult") String matchresult) {
        log.info("앱에서 경기 결과 입력중..");
        ArrayList<String> result = new ArrayList<>();
        String otherteam = null;
        ReserveDTO dto = rService.findReserve(field, date, time);
        if(dto.getNameA().equals(myteam)){
            otherteam = dto.getNameB();
        } else {
            otherteam = dto.getNameA();
        }
        TeamDTO matchedteam = tService.findBytName(otherteam);
        TeamDTO ourteam = tService.findBytName(myteam);
        matchedteam.setTManner(matchedteam.getTManner() + mannerscore);
        try {
            matchedteam.setTMannercnt(matchedteam.getTMannercnt() + 1);
        } catch (Exception e) {
            matchedteam.setTMannercnt(1);
        }
        String nickNameA;
        String nickNameB;

        if (matchresult.equals("undefined")) { // 어웨이

        } else { //홈
            ArrayList<RecordVO> records = new ArrayList<RecordVO>();
            RecordVO record = new RecordVO();

            try {
                records = matchedteam.getRecords();
            } catch (Exception e) {
                records = new ArrayList<RecordVO>();
            }
            matchedteam.setRecords(records);

            ArrayList<RecordVO> mrecords = matchedteam.getRecords();
            ArrayList<RecordVO> orecords = ourteam.getRecords();

            for (int i = 0; i < mrecords.size(); i++) {
                if (mrecords.get(i).getField().equals(field) && mrecords.get(i).getDate().equals(date) && mrecords.get(i).getTime().equals(time)) {
                    if (matchresult.equals("무")) {
                        mrecords.get(i).setResult(matchresult);
                        log.info("상대팀 결과 : " + mrecords.get(i).getResult());
                    } else if (matchresult.equals("승")) {
                        mrecords.get(i).setResult("패");
                        log.info("상대팀 결과 : " + mrecords.get(i).getResult());
                    } else if (matchresult.equals("패")) {
                        mrecords.get(i).setResult("승");
                        log.info("상대팀 결과 : " + mrecords.get(i).getResult());
                    }
                }
                matchedteam.setRecords(mrecords);
            }
            for (int i = 0; i < orecords.size(); i++) {
                if (orecords.get(i).getField().equals(field) && orecords.get(i).getDate().equals(date) && orecords.get(i).getTime().equals(time)) {
                    orecords.get(i).setResult(matchresult);
                    log.info("우리팀 결과 : " + orecords.get(i).getResult());
                }
                ourteam.setRecords(orecords);
            }
        }
        ArrayList<ReserveDTO> allreserve = rService.findAll();
        for (int i = 0; i < allreserve.size(); i++) {
            if (allreserve.get(i).getField().equals(field) && allreserve.get(i).getDate().equals(date) && allreserve.get(i).getTime().equals(time)) {
                nickNameA = allreserve.get(i).getNickNameA(); // 홈팀 예약자
                nickNameB = allreserve.get(i).getNickNameB(); // 어웨이팀 예약자
                log.info("홈팀예약자 : " + nickNameA + "// 어웨이팀 예약자 : " + nickNameB);

                if (matchresult.equals("undefined")) {
                    ReservationDTO awayrv = rvService.findByid(nickNameB);
                    for (int j = 0; j < awayrv.getInfo().size(); j++) {
                        if (awayrv.getInfo().get(j).getField().equals(field) && awayrv.getInfo().get(j).getDate().equals(date) && awayrv.getInfo().get(j).getTime().equals(time)) {
                            awayrv.getInfo().get(j).setMannerornot("완료");
                            log.info("어웨이팀 예약자의 reservation상태 : " + awayrv.getInfo().get(j));
                        }
                    }
                    rvService.save(awayrv);
                } else {
                    ReservationDTO homerv = rvService.findByid(nickNameA);
//            log.info("homerv : " + homerv);
                    for (int j = 0; j < homerv.getInfo().size(); j++) {
                        if (homerv.getInfo().get(j).getField().equals(field) && homerv.getInfo().get(j).getDate().equals(date) && homerv.getInfo().get(j).getTime().equals(time)) {
                            log.info("홈팀 예약자의 reservation상태 전 : + " + homerv.getInfo().get(j));
                            homerv.getInfo().get(j).setMannerornot("완료");
                            log.info("홈팀 예약자의 reservation상태 후: " + homerv.getInfo().get(j));
                        }
                    }
                    rvService.save(homerv);
                }
            }
        }
        tService.save(matchedteam);
        tService.save(ourteam);
        log.info("앱에서 매너 기입 완료");
        result.add("done");
        return result;
    }

    @RequestMapping(value = "/postReportBoard", method = RequestMethod.POST)
    public ArrayList<String> post_report_board(@RequestParam("bNo") String bNo, @RequestParam("reportNickName") String reportNickName, @RequestParam("reportMessage") String reportMessage){
        CommunityDTO reportedBoard = cService.findByIdx(Integer.parseInt(bNo));
        ArrayList<String> result = new ArrayList<>();
        ArrayList<String> reportNickNames = reportedBoard.getReportNickNames();
        ArrayList<String> reportMessages = reportedBoard.getReportMessages();
        if(reportNickNames.size() > 0){
            for(int i = 0; i < reportNickNames.size(); i++){
                if(reportNickNames.get(i).equals(reportNickName)){
                    result.add("alreadyDone");
                }
            }
            if(result.size() == 0){
                reportNickNames.add(reportNickName);
                reportMessages.add(reportMessage);
                result.add("done");
            }
        } else {
            reportNickNames.add(reportNickName);
            reportMessages.add(reportMessage);
            result.add("done");
        }
        reportedBoard.setReportNickNames(reportNickNames);
        reportedBoard.setReportMessages(reportMessages);
        cService.save(reportedBoard);
        return result;
    }

    @RequestMapping(value = "/postBlockUser", method = RequestMethod.POST)
    public MemberDTO post_block_user(@RequestParam("myNickName") String myNickName, @RequestParam("blockNickName") String blockNickName){
        MemberDTO myInfo = mService.findByNickName(myNickName);
        ArrayList<String> blockList = new ArrayList<>();
        try {
            blockList = myInfo.getBlockList();
            blockList.add(blockNickName);
        } catch (Exception e){
            blockList.add(blockNickName);
        }
        myInfo.setBlockList(blockList);
        mService.save(myInfo);

        return myInfo;
    }

}