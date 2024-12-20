package com.ai.controller;

import com.ai.domain.*;

import com.ai.service.*;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.bson.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpSession;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

@Slf4j
@Controller

public class GreetingController {

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




    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public String greeting(Manner message, SimpMessageHeaderAccessor messageHeaderAccessor) throws Exception {
        Thread.sleep(500); // simulated delay
        String useremail = (String)messageHeaderAccessor.getSessionAttributes().get("sessionId");
        log.info("메세지매핑 /hello에 sendTo - /topic/greetings 리턴 값 : " + "Hello, " + useremail + "님 !");
        return ("Hello, " + useremail + "님 !");
//        return useremail;
//        return new Greeting("hello, " + HtmlUtils.htmlEscape(message.getName()));
    }

    @MessageMapping("/manner")
    @SendTo("/topic/greetings")
    public JsonObject manner(JsonObject jsObject
            , SimpMessageHeaderAccessor messageHeaderAccessor) throws Exception {
        log.info(jsObject.toString());
        Thread.sleep(500); // simulated delay
        log.info("메세지매핑 /manner에 sendTO - /topic/greetings 들어옴.,,,");
        HttpSession useremail = (HttpSession)messageHeaderAccessor.getSessionAttributes().get("sessionId");
//        log.info(message.getNickName());
//        return ("Hello, " + useremail + "님 ! " + message.getNickName() + " / " + message.getField() + " / " + message.getDate() + " / " + message.getTime() + " / ");
//        return useremail;
        return jsObject;
    }

    @MessageMapping("/privatemanner")
    @SendToUser("/queue/greetings")
    public ArrayList<Manner> manner2(@RequestBody HashMap<String, ArrayList<Manner>> map
            , SimpMessageHeaderAccessor messageHeaderAccessor) throws Exception {
        log.info("@MessageMapping(\"/privatemanner\") 들어옴,,,");
        Manner resManner = new Manner();
        ArrayList<Manner> resMannerList = new ArrayList<>();
        ArrayList<Manner> messageList = map.get("message");
        Thread.sleep(500); // simulated delay
        String useremail = (String)messageHeaderAccessor.getSessionAttributes().get("sessionId");
        MemberDTO member = mService.findByid(useremail);

        // 멤버의 알람필드 채우기시작
        ZonedDateTime nowSeoul = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        String parsedcurDateTime = nowSeoul.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); //현재시간을 String으로 파씽
        Integer alarmCount = member.getAlarmCount();

        ArrayList<AlarmVO> alarms = new ArrayList<AlarmVO>();
        try{
            alarms = member.getAlarms();
            for(int i=0;i<messageList.size();i++){
                AlarmVO alarm = new AlarmVO();
                TeamDTO matchedTeam = tService.findBytName(mService.findByNickName(messageList.get(i).getNickName()).getTName());
                alarm.setSender(matchedTeam.getTName());
                alarm.setReceiver(member.getNickName());
                alarm.setSendDate(parsedcurDateTime);
                alarm.setIsRead("안읽음");
                alarm.setAlarmType("매너");
                alarm.setMessage(messageList.get(i).getField() + "\n" + messageList.get(i).getDate() + " " +
                        messageList.get(i).getTime() + "경기\n상대팀 : " + matchedTeam.getTName());
                alarms.add(alarm);
                alarmCount += 1;
                resManner.setMessage(messageList.get(i).getField() + "\n" + messageList.get(i).getDate() + " " +
                        messageList.get(i).getTime() + "경기\n상대팀 : " + matchedTeam.getTName());
                resManner.setType("매너");
                resMannerList.add(resManner);
            }

        }catch (Exception e){
            alarms = new ArrayList<AlarmVO>();
            for(int i=0;i<messageList.size();i++){
                AlarmVO alarm = new AlarmVO();
                TeamDTO matchedTeam = tService.findBytName(mService.findByNickName(messageList.get(i).getNickName()).getTName());
                alarm.setSender(matchedTeam.getTName());
                alarm.setReceiver(member.getNickName());
                alarm.setSendDate(parsedcurDateTime);
                alarm.setIsRead("안읽음");
                alarm.setAlarmType("매너");
                alarm.setMessage(messageList.get(i).getField() + "\n" + messageList.get(i).getDate() + " " +
                        messageList.get(i).getTime() + "경기\n상대팀 : " + matchedTeam.getTName());
                alarms.add(alarm);
                alarmCount += 1;
                resManner.setMessage(messageList.get(i).getField() + "\n" + messageList.get(i).getDate() + " " +
                        messageList.get(i).getTime() + "경기\n상대팀 : " + matchedTeam.getTName());
                resManner.setType("매너");
                resMannerList.add(resManner);
            }
        }
        member.setAlarmCount(alarmCount);
        member.setAlarms(alarms);
        mService.save(member);
        Integer unreadalarmCount;
        try{
            unreadalarmCount = alarmCount - member.getReadCount();
        }catch (Exception e){
            unreadalarmCount = alarmCount - 0;
        }
        // 멤버의 알람필드 채우기끝
//        return member;
        resMannerList.get(0).setUnreadalarmcount(unreadalarmCount);

        // FireBase
        Firebase firebase = new Firebase();
        if (member.getFireBaseToken() != null) {
            try {
                log.info("웹 -> 앱으로 보내는 매너점수기입 알람 성공 : " + firebase.sendToToken(member.getFireBaseToken(), "매너점수를 기입하세요.", "홈 >> 예약에서 확인하기"));
            } catch (FirebaseMessagingException e) {
                log.info("웹 -> 앱으로 보내는 매너점수기입 알람 실패 : " + e.getMessage());
            }
        }


        return resMannerList;
    }

    @MessageMapping("/accept")
//    @SendToUser("/queue/away")// send 함수
    public void accept(@RequestBody HashMap<String, Manner> map
            , SimpMessageHeaderAccessor messageHeaderAccessor) throws Exception {
        Manner message = map.get("message");
        ArrayList<String> tokenList = new ArrayList<>();
        log.info("GreetingController - accept로 들어옴,,, 받은 값 확인 : " + message);
        String useremail = (String)messageHeaderAccessor.getSessionAttributes().get("sessionId");
        ZonedDateTime nowSeoul = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        String parsedcurDateTime = nowSeoul.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); //현재시간을 String으로 파씽
        String tName = mService.findByid(useremail).getTName();
        TeamDTO hometeam = tService.findBytName(message.getNameA());
        String awayteamname = message.getNameB();
        TeamDTO awayteam = tService.findBytName(message.getNameB());


        for(int i=0;i<awayteam.getTeamMember().size();i++){
            MemberDTO awayteammem = mService.findByNickName(awayteam.getTeamMember().get(i));
            ArrayList<AlarmVO> awaymemAlarmList = new ArrayList<>();
            AlarmVO alarm = new AlarmVO();
            alarm.setSender(tName);
            alarm.setReceiver(awayteam.getTName());
            alarm.setIsRead("안읽음");
            alarm.setAlarmType("어웨이");
            alarm.setSendDate(parsedcurDateTime);
            alarm.setMessage(message.getField() + "\n" + message.getDate() + " " +
                    message.getTime() + "경기\n홈팀 : " + tName);
            try{
                awayteammem.setAlarmCount(awayteammem.getAlarmCount()+1);
                awaymemAlarmList = awayteammem.getAlarms();
                awaymemAlarmList.add(alarm);
            }catch (Exception e){
                awaymemAlarmList = new ArrayList<>();
                awaymemAlarmList.add(alarm);
            }
            awayteammem.setAlarms(awaymemAlarmList);
            mService.save(awayteammem);

            Manner resManner = new Manner();
            resManner.setUnreadalarmcount(awayteammem.getAlarmCount()-awayteammem.getReadCount());
            resManner.setMessage(message.getField() + "\n" + message.getDate() + " " +
                    message.getTime() + "경기\n홈팀 : " + tName);
            simpMessagingTemplate.convertAndSend("/queue/away/" + awayteammem.getNickName(), resManner);
            log.info("어웨이로 보내는 도착지 && 메세지 : " + "/queue/away/" + awayteammem.getNickName() + " && " + resManner.getMessage());

            if (awayteammem.getFireBaseToken() != null) {
                tokenList.add(awayteammem.getFireBaseToken());
            }
        }
        for(int i=0;i<hometeam.getTeamMember().size();i++){
            MemberDTO hometeammem = mService.findByNickName(hometeam.getTeamMember().get(i));
            log.info("홈팀의 누구한테 알람보낼거니 : " + hometeammem.getNickName());
            ArrayList<AlarmVO> homememAlarmList = new ArrayList<>();
            AlarmVO alarm = new AlarmVO();
            alarm.setSender(tName);
            alarm.setReceiver(tName);
            alarm.setIsRead("안읽음");
            alarm.setAlarmType("어웨이");
            alarm.setSendDate(parsedcurDateTime);
            alarm.setMessage(message.getField() + "\n" + message.getDate() + " " +
                    message.getTime() + "경기\n어웨이팀 : " +  awayteamname);
            try{
                hometeammem.setAlarmCount(hometeammem.getAlarmCount()+1);
                homememAlarmList = hometeammem.getAlarms();
                homememAlarmList.add(alarm);
            }catch (Exception e){
                homememAlarmList = new ArrayList<>();
                homememAlarmList.add(alarm);
            }
            hometeammem.setAlarms(homememAlarmList);
            mService.save(hometeammem);

            Manner resManner = new Manner();
            resManner.setUnreadalarmcount(hometeammem.getAlarmCount()-hometeammem.getReadCount());
            resManner.setMessage(message.getField() + "\n" + message.getDate() + " " +
                    message.getTime() + "경기\n어웨이팀 : " +  awayteamname);
            simpMessagingTemplate.convertAndSend("/queue/away/" + hometeammem.getNickName(), resManner);
            log.info("홈으로 보내는 도착지 && 메세지 : " + "/queue/away/" + hometeammem.getNickName() + " && " + resManner.getMessage());
            if (hometeammem.getFireBaseToken() != null) {
                tokenList.add(hometeammem.getFireBaseToken());
            }
        }

        log.info("tokenList : " + tokenList);
        // FireBase
        Firebase firebase = new Firebase();
        try {
            log.info("웹 -> 앱으로 보내는 홈팀의 어웨이팀 수락 알람 성공 : " + firebase.multipleSendToToken(tokenList, "매칭이 완료되었어요!", hometeam.getTName() + " vs " + awayteam.getTName() + "\n홈 >> 예약에서 확인하기"));
        } catch (FirebaseMessagingException e) {
            log.info("웹 -> 앱으로 보내는 홈팀의 어웨이팀 수락 알람 실패 : " + e.getMessage());
        }

        ArrayList<RecordVO> records = new ArrayList<RecordVO>();
        RecordVO record = new RecordVO();
        record.setField(message.getField());
        record.setDate(message.getDate());
        record.setTime(message.getTime());
        record.setVsTeam(awayteam.getTName());
        record.setResult(null);
        try{
            records = hometeam.getRecords();
            records.add(record);
        }catch (Exception e){
            records = new ArrayList<RecordVO>();
            records.add(record);
        }
        hometeam.setRecords(records);
        tService.save(hometeam);
        ArrayList<RecordVO> records2 = new ArrayList<RecordVO>();
        RecordVO record2 = new RecordVO();
        record2.setField(message.getField());
        record2.setDate(message.getDate());
        record2.setTime(message.getTime());
        record2.setVsTeam(hometeam.getTName());
        record2.setResult(null);
        records2.add(record2);
        awayteam.setRecords(records2);
        tService.save(awayteam);

    }

    @MessageMapping("/sendtohometeam")
    public void sendTOhometeam(@RequestBody HashMap<String, Manner> map
            , SimpMessageHeaderAccessor messageHeaderAccessor) throws Exception {
        Manner message = map.get("message");
        log.info("GreetingController - sendTOhometeam 들어옴,,, 받은 값 확인 : " + message);
        String useremail = (String)messageHeaderAccessor.getSessionAttributes().get("sessionId");
        MemberDTO mem = mService.findByid(useremail);
        String hometeamname = message.getHometeam();
        TeamDTO hometeam = tService.findBytName(hometeamname);
        log.info("보낼도착지 : " + hometeam.getTName() + "/ 구장정보 : " + message.getField() + message.getDate() + message.getTime() + " 도전신청자 : " + mem.getNickName() + "도전팀 : " + mem.getTName());

        // 멤버에 알람필드 채우기시작

        ArrayList<AlarmVO> alarms = mem.getAlarms();
        ZonedDateTime nowSeoul = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        String parsedcurDateTime = nowSeoul.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); //현재시간을 String으로 파씽
//        Integer alarmCount = mem.getAlarmCount();
//        alarmCount += 1;
//        mem.setAlarmCount(alarmCount);
        AlarmVO alarm = new AlarmVO();

        alarm.setSender(mem.getTName());
        alarm.setReceiver(hometeamname);
        alarm.setSendDate(parsedcurDateTime);
        alarm.setIsRead("안읽음");
        alarm.setAlarmType("홈");
        alarm.setMessage(message.getField() + "\n" + message.getDate() + " " +
                message.getTime() + "경기\n도전팀 : " + mem.getTName());


        MemberDTO hometeamreservationmember = mService.findByNickName(rService.findReserve(message.getField(), message.getDate(), message.getTime()).getNickNameA());
        ArrayList<AlarmVO> hometeamMemalarms = new ArrayList<AlarmVO>();
        try{
            hometeamreservationmember.setAlarmCount(hometeamreservationmember.getAlarmCount()+1);
            hometeamMemalarms = hometeamreservationmember.getAlarms();
            log.info("이 사람이 알람을 한번이라도 받은 적 있음 : " + hometeamMemalarms.get(0).getMessage());
            hometeamMemalarms.add(alarm);
        }catch (Exception e){
            hometeamMemalarms = new ArrayList<AlarmVO>();
            hometeamMemalarms.add(alarm);
        }
        hometeamreservationmember.setAlarms(hometeamMemalarms);
        mService.save(hometeamreservationmember);
        ArrayList<Manner> resMannerList = new ArrayList<>();
        Manner resManner = new Manner();
        resManner.setUnreadalarmcount(hometeamreservationmember.getAlarmCount()-hometeamreservationmember.getReadCount());
        resManner.setMessage(message.getField() + "\n" + message.getDate() + " " +
                message.getTime() + "경기\n도전팀 : " + mem.getTName());
        simpMessagingTemplate.convertAndSend("/queue/home/" + hometeamreservationmember.getNickName(), resManner);


        // FireBase
        Firebase firebase = new Firebase();
        if (hometeamreservationmember.getFireBaseToken() != null) {
            try {
                log.info("웹 -> 앱으로 보내는 도전신청 알람 성공 : " + firebase.sendToToken(hometeamreservationmember.getFireBaseToken(), "도전 신청이 도착했어요", "홈 >> 예약에서 확인하기"));
                log.info("누구한테 보내냐면 ~? : " + hometeamreservationmember.getNickName());
            } catch (FirebaseMessagingException e) {
                log.info("웹 -> 앱으로 보내는 도전신청 알람 실패 : " + e.getMessage());
            }
        }
    }
}