package com.ai.domain;

import com.ai.service.*;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

@Component
@Slf4j
public class Scheduler {

    @Autowired
    ReservationService rvService;
    @Autowired
    TeamService tService;

    @Autowired
    MemberService mService;

    @Autowired
    ReserveService rService;

    @Autowired
    WeatherService service;

    @Autowired
    FieldService fService;

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;


    @Scheduled(fixedDelay = 60000)
    public void SchedulerTest() throws ParseException {
        if (CheckDevenv.DEVENV == false) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            ZonedDateTime nowSeoul = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
            String parsedcurDateTime = nowSeoul.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); //현재시간을 String으로 파씽
            int hours = nowSeoul.getHour();
            String nowDate = nowSeoul.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            ArrayList<String> tokenList = new ArrayList<>();
            ArrayList<ReservationDTO> rvList = rvService.findAll();
            for (int i = 0; i < rvList.size(); i++) {
                for (int j = 0; j < rvList.get(i).getInfo().size(); j++) {
                    ReservationVO rV = rvList.get(i).getInfo().get(j);
                    String[] timeArray = rV.getTime().split("시 - ");
                    String[] timeArray2 = timeArray[1].split("시");
                    String refund = null;
                    Date before = new Date();
                    try {
                        before = sdf.parse(rV.getDate());
                    } catch (Exception e) {
                        log.info(e.getMessage());
                    }
                    Date now = sdf.parse(nowDate);
                    if (hours >= Integer.parseInt(timeArray2[0]) && nowDate.equals(rV.getDate()) && rV.getState().equals("매칭 대기중")) {
                        rV.setState("매칭 취소");
                        rvList.get(i).getInfo().set(j, rV);
                        log.info("시간에 의해 매칭취소된 구장 정보");
                        log.info("취소된 예약 : " + rV.getField() + ", " + rV.getDate() + ", " + rV.getTime());
                    }
                    if (before.before(now) && rV.getState().equals("매칭 대기중")) {
                        rV.setState("매칭 취소");
                        rvList.get(i).getInfo().set(j, rV);
                        log.info("시간에 의해 매칭취소된 구장 정보");
                        log.info("취소된 예약 : " + rV.getField() + ", " + rV.getDate() + ", " + rV.getTime());
                    }

                    if (hours >= Integer.parseInt(timeArray2[0]) && nowDate.equals(rV.getDate()) && rV.getState().equals("매칭 완료")) {
//					if (nowDate.equals(rV.getDate()) && rV.getState().equals("매칭 완료")) {

                        TeamDTO dto = tService.findBytName(mService.findByNickName(rvList.get(i).getId()).getTName());
                        refund = rV.getRefund(); // reservation의 환불상태를 가져옴 -> 환불상태가 비어있으면 catch로 감
                        String manner = null;
                        manner = rV.getManner();
                        if (manner == null) {
                            refund = "환불 불가";
                            int total = dto.getTTotal();
                            log.info("올라가기전 팀의 전적 : " + dto.getTTotal());
                            dto.setTTotal(total + 1);
                            rV.setRefund(refund);
                            rV.setManner("매너알람완료");
                            rvList.get(i).getInfo().set(j, rV);
                            log.info("전적이 올라가나요 ? : " + dto.getTName() + " 팀의 전적 : " + dto.getTTotal() + " 해당예약의환불상태 : " + rV.getRefund());
                            log.info("rv겟타입 : " + rV.getType());
                            if (rV.getType().equals("홈")) {
                                // 전적기입알람시작
                                log.info("홈전적기입알람코드시작==============================================================================");
                                ReserveDTO reservedto = rService.findReserve(rV.getField(), rV.getDate(), rV.getTime());
                                String hometeamname = reservedto.getNameA();
                                MemberDTO hometeamcpt = mService.findByNickName(reservedto.getNickNameA()); //예약자
                                log.info("홈예약자 : " + hometeamcpt.getNickName());
                                TeamDTO hometeam = tService.findBytName(hometeamname);
                                log.info("hometeam : " + hometeam.getTName());
                                for (int z = 0; z < hometeam.getRecords().size(); z++) {
                                    RecordVO record = hometeam.getRecords().get(z);
                                    if (record.getField().equals(rV.getField()) && record.getDate().equals(rV.getDate()) && record.getTime().equals(rV.getTime()) && record.getResult() == null) {
                                        log.info("전적기입알람발송조건통과==============================================================================");

                                        ArrayList<AlarmVO> alarms = new ArrayList<>();
                                        AlarmVO alarm = new AlarmVO();
                                        alarm.setSender("fsmanager");
                                        alarm.setReceiver(hometeamcpt.getNickName());
                                        alarm.setIsRead("안읽음");
                                        alarm.setAlarmType("전적기입");
                                        alarm.setSendDate(parsedcurDateTime);
                                        alarm.setMessage(rV.getField() + "\n" + rV.getDate() + " " +
                                                rV.getTime() + "경기\n매칭상대팀 : " + record.getVsTeam());
                                        Manner resManner = new Manner();
                                        alarms = hometeamcpt.getAlarms();
                                        alarms.add(alarm);
                                        hometeamcpt.setAlarmCount(hometeamcpt.getAlarmCount() + 1);
                                        hometeamcpt.setAlarms(alarms);
                                        mService.save(hometeamcpt);
                                        resManner.setUnreadalarmcount(hometeamcpt.getAlarmCount() - hometeamcpt.getReadCount());
                                        resManner.setMessage(rV.getField() + "\n" + rV.getDate() + " " +
                                                rV.getTime() + "경기\n매칭상대팀 : " + record.getVsTeam());
                                        resManner.setField(rV.getField());
                                        resManner.setDate(rV.getDate());
                                        resManner.setTime(rV.getTime());
                                        resManner.setNameA(hometeamname);
                                        resManner.setNameB(reservedto.getNameB());
                                        simpMessagingTemplate.convertAndSend("/queue/winorlose/" + hometeamcpt.getNickName(), resManner);
                                        log.info("전적기입알람발송완료(홈) 도착지 && 내용 : " + "/queue/winorlose/" + hometeamcpt.getNickName() + " && " + resManner.getMessage());
                                        // 전적기입알람끝
                                        tokenList.add(hometeamcpt.getFireBaseToken());
                                        // FireBase
                                    }
                                    tService.save(dto);
                                }
                            } else if (rV.getType().equals("어웨이")) {
                                log.info("어웨이전적기입알람코드시작==============================================================================");
                                ReserveDTO reservedto = rService.findReserve(rV.getField(), rV.getDate(), rV.getTime());
                                String awayteamname = reservedto.getNameB();
                                MemberDTO awayteamreservemem = mService.findByNickName(reservedto.getNickNameB()); //예약자
                                TeamDTO hometeam1 = tService.findBytName(mService.findByNickName(reservedto.getNickNameA()).getTName());
                                log.info("어웨이예약자 : " + awayteamreservemem.getNickName());
                                TeamDTO awayteam = tService.findBytName(awayteamname);
                                log.info("awayteam : " + awayteam.getTName());
                                log.info("해당예약 : " + reservedto.getField() + "&&" + reservedto.getDate() + "&&" + reservedto.getTime());
                                RecordVO record = new RecordVO();


                                for (int z = 0; z < awayteam.getRecords().size(); z++) {
                                    record = awayteam.getRecords().get(z);
                                    if (record.getField().equals(rV.getField()) && record.getDate().equals(rV.getDate()) && record.getTime().equals(rV.getTime()) && record.getResult() == null) {
                                        log.info("전적기입알람발송조건통과==============================================================================");

                                        ArrayList<AlarmVO> alarms = new ArrayList<>();
                                        AlarmVO alarm = new AlarmVO();
                                        alarm.setSender("fsmanager");
                                        alarm.setReceiver(awayteamreservemem.getNickName());
                                        alarm.setIsRead("안읽음");
                                        alarm.setAlarmType("전적기입");
                                        alarm.setSendDate(parsedcurDateTime);
                                        alarm.setMessage(rV.getField() + "\n" + rV.getDate() + " " +
                                                rV.getTime() + "경기\n매칭상대팀 : " + record.getVsTeam());
                                        Manner resManner = new Manner();
                                        alarms = awayteamreservemem.getAlarms();
                                        alarms.add(alarm);
                                        awayteamreservemem.setAlarmCount(awayteamreservemem.getAlarmCount() + 1);
                                        awayteamreservemem.setAlarms(alarms);
                                        mService.save(awayteamreservemem);
                                        resManner.setUnreadalarmcount(awayteamreservemem.getAlarmCount() - awayteamreservemem.getReadCount());
                                        resManner.setMessage(rV.getField() + "\n" + rV.getDate() + " " +
                                                rV.getTime() + "경기\n매칭상대팀 : " + record.getVsTeam());
                                        resManner.setField(rV.getField());
                                        resManner.setDate(rV.getDate());
                                        resManner.setTime(rV.getTime());
                                        resManner.setNameA(awayteamname);
                                        resManner.setNameB(reservedto.getNameB());
                                        simpMessagingTemplate.convertAndSend("/queue/winorlose/" + awayteamreservemem.getNickName(), resManner);
                                        log.info("전적기입알람발송완료(어웨이) 도착지 && 내용 : " + "/queue/winorlose/" + awayteamreservemem.getNickName() + " && " + resManner.getMessage());
                                        // 전적기입알람끝
                                        tokenList.add(awayteamreservemem.getFireBaseToken());
                                        // FireBase
                                    }
                                    tService.save(dto);
                                }
                            }

                        }
                    }
                    rvService.save(rvList.get(i));
                }
            }
            Firebase firebase = new Firebase();
            try {
//				log.info("홈팀예약자에게 승부 기록 알림 성공 : " + firebase.multipleSendToToken(tokenList, "매치 결과를 기다리고 있어요", "홈 >> 예약에서 확인하기"));
                firebase.multipleSendToToken(tokenList, "매치 결과를 기다리고 있어요", "홈 >> 예약에서 확인하기");
            } catch (FirebaseMessagingException e) {
                log.info("홈팀예약자에게 승부 기록 알림 실패 : " + e.getMessage());
            }
        }
    }


}

