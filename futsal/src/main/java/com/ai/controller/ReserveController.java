package com.ai.controller;


import com.ai.domain.*;
import com.ai.service.*;
import com.google.firebase.messaging.FirebaseMessagingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Slf4j
@Controller
@Api(tags = {"예약관련 API"})
@RequestMapping(path = "/reserveTo")
public class ReserveController {
	@Autowired
	ReserveService service;

	@Autowired
	MemberService mService;

	@Autowired
	FieldService fService;

	@Autowired
	ReserveListService rlService;

	@Autowired
	ReservationService rvService;

	@Autowired
	SimpMessagingTemplate simpMessagingTemplate;

	@Autowired
	TeamService tService;


	@RequestMapping(value = "/checkAjax", method = RequestMethod.POST)
	@ApiOperation(value = "해당 구장에 예약이 있는지 없는지 판단", response = ReserveDTO.class)
	@ResponseBody
	public String checkAjax(@RequestParam("field") String field, @RequestParam("fTime") String fTime, @RequestParam("fDate") String fDate) {
		String out = "";

		try {
			if (service.findReserve(field, fDate, fTime).getState() == null || service.findReserve(field, fDate, fTime).getState().isEmpty()) {
				out = "null";
			} else {
				out = service.findReserve(field, fDate, fTime).getState();
			}
		} catch (NullPointerException e) {
			out = "null";
		}
		return out;
	}

	@RequestMapping(value = "/checkAjax2", method = RequestMethod.POST)
	@ApiOperation(value = "해당 구장에 본인 예약이 있는지 없는지 판단", response = ReserveDTO.class)
	@ResponseBody
	public String checkAjax2(@RequestParam("field") String field, @RequestParam("fTime") String fTime, @RequestParam("fDate") String fDate, HttpSession session) {
		String out = "";
		log.info("예약정보 주석처리 함 : /reserveTo/checkAjax2");
        /*log.info("==== 조회한 예약 정보 ====");
        log.info("홈팀 신청자 : " + service.findReserve(field, fDate, fTime).getNickNameA());
        log.info("어웨이팀 신청자 : " + mService.findByid((String) session.getAttribute("userId")).getNickName());*/
		if (service.findReserve(field, fDate, fTime).getNameA().equals(mService.findByid((String) session.getAttribute("userId")).getTName())) {
			out = "true";
		} else {
			out = "false";
		}
		return out;
	}


	@ResponseBody
	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	@ApiOperation(value = "예약 등록", response = ReserveDTO.class)
	public Integer insertReserve(@RequestParam("fName") String fName, @RequestParam("fPrice") String fPrice, @RequestParam("fDate") String fDate,
								 @RequestParam("fTime") String fTime, @RequestParam("fPhoneNo") String fPhoneNo, @RequestParam("type") String type,
								 @RequestParam("rState") String rState, @RequestParam("nickName1") String nickName1, @RequestParam("tName1") String tName1,
								 @RequestParam("rDate1") String rDate1, @RequestParam("nickName2") String nickName2, @RequestParam("tName2") String tName2,
								 @RequestParam("rDate2") String rDate2, HttpSession session) {
		String nickName = mService.findByid((String) session.getAttribute("userId")).getNickName();
		ReserveDTO reserve = new ReserveDTO();
		reserve.setField(fName);
		reserve.setPrice(fPrice);
		Integer price = Integer.parseInt(fPrice.replace(",", "").replace(" P", ""));
		reserve.setDate(fDate);
		reserve.setTime(fTime);
		reserve.setPhoneNo(fPhoneNo);
		reserve.setType(type);
		if (type.equalsIgnoreCase("all")) {
			reserve.setState("B");
		} else {
			reserve.setState(rState);
		}

		reserve.setNickNameA(nickName1);
		reserve.setNameA(tName1);
		reserve.setDateA(rDate1);
		reserve.setNickNameB(nickName2);
		reserve.setNameB(tName2);
		reserve.setDateB(rDate2);
		MemberDTO member = mService.findByNickName(nickName1);
		member.setHadPoint(member.getHadPoint() - price);
		mService.save(member);
		service.insert(reserve);

		ReservationVO reservation = new ReservationVO();
		ArrayList<ReservationVO> reservationList = new ArrayList<ReservationVO>();
		if (reserve.getNickNameA().equals(nickName)
				|| reserve.getNickNameB().equals(nickName)) {
			if (type.equalsIgnoreCase("part")) {
				if (reserve.getNickNameA().equals(nickName)) {
					reservation.setDate(reserve.getDate());
					reservation.setRegDate(reserve.getDateA());
					reservation.setState("매칭 대기중");
					reservation.setType("홈");
				} else if (reserve.getNickNameB().equals(nickName)) {
					reservation.setDate(reserve.getDate());
					reservation.setRegDate(reserve.getDateB());
					reservation.setState("매칭 완료");
					reservation.setType("어웨이");
				}
			} else if (reserve.getType().equalsIgnoreCase("all")) {
				reservation.setDate(reserve.getDate());
				reservation.setRegDate(reserve.getDateA());
				reservation.setType("홈 & 어웨이");
				reservation.setState("매칭 완료");
			}
			reservation.setTime(reserve.getTime());
			reservation.setField(reserve.getField());
			reservation.setPrice(reserve.getPrice());
			reservation.setFieldPic(fService.findByfName(fName).getFPic1());
			reservationList.add(reservation);
		}

		try {
			ReservationDTO reservationDTO = rvService.findByid(nickName);
			reservationDTO.getInfo().addAll(reservationList);
			rvService.save(reservationDTO);
		} catch (Exception e) {
			log.info("Reservation 디비에 등록된 정보가 없습니다.");
			rvService.save(new ReservationDTO(nickName, reservationList));
		}
		return 1;
	}

	@RequestMapping(value = "/saveAway", method = RequestMethod.POST)
	@ApiOperation(value = "도전예약 등록", response = ReserveDTO.class)
	@ResponseBody
	public Integer saveAway(
			@RequestParam("field") String field, @RequestParam("fTime") String fTime, @RequestParam("fDate") String fDate,
			@RequestParam("nickName2") String nickName2, @RequestParam("tName2") String tName2, @RequestParam("rDate2") String rDate2, HttpSession session) {
		ReserveDTO reserve = service.findReserve(field, fDate, fTime);
		ReservationDTO yukidto = rvService.findByid(reserve.getNickNameA());
		ArrayList<ReservationVO> yuki = yukidto.getInfo();
		for (int i = 0; i < yuki.size(); i++) {
			if (yuki.get(i).getField().equals(field) && yuki.get(i).getDate().equals(fDate) && yuki.get(i).getTime().equals(fTime)) {
				yuki.get(i).setState("매칭 완료");
				yuki.get(i).setType("홈 & 어웨이");
			}
		}
		yukidto.setInfo(yuki);
		rvService.save(yukidto);

		reserve.setNickNameB(nickName2);
		reserve.setType("all");
		reserve.setState("B");
		reserve.setNameB(tName2);
		reserve.setDateB(rDate2);
		service.save(reserve);
		String nickName = mService.findByid((String) session.getAttribute("userId")).getNickName();
		ReservationVO reservation = new ReservationVO();
		ArrayList<ReservationVO> reservationList = new ArrayList<ReservationVO>();
		reservation.setDate(reserve.getDate());
		reservation.setRegDate(reserve.getDateA());
		reservation.setTime(reserve.getTime());
		reservation.setField(reserve.getField());
		reservation.setPrice(reserve.getPrice());
		reservation.setType("홈 & 어웨이");
		reservation.setState("매칭 완료");
		reservationList.add(reservation);

		ReservationDTO reservationDTO = rvService.findByid(nickName);
		int size = reservationDTO.getInfo().size();
		for (int i = 0; i < size; i++) {
			if (reservationDTO.getInfo().get(i).getField().equals(reservationList.get(0).getField())
					&& reservationDTO.getInfo().get(i).getTime().equals(reservationList.get(0).getTime())
					&& reservationDTO.getInfo().get(i).getDate().equals(reservationList.get(0).getDate())) {
				reservationDTO.getInfo().remove(i);
				log.info("기존 예약 제거");
			}
		}
		reservationDTO.getInfo().addAll(reservationList);
		rvService.save(reservationDTO);

		return 1;
	}

	@RequestMapping(value = "/updateDeleted", method = RequestMethod.POST)
	@ApiOperation(value = "홈팀의 도전팀 도전 수락 및 매칭성사", response = ReserveDTO.class)
	public String updateReserve(@RequestParam("rlList") String data, HttpSession session) {
		ArrayList<String> tokenList = new ArrayList<>();

		// form에서 넘어온 값 정리
		String datas = data.replaceAll("\\t", ",");
		String[] arrDatas = datas.split(",");
//         arrDatas[0] : 구장사용일자 | arrDatas[1] : 사용시간 | arrDatas[2] : 구장명 | arrDatas[3] : awayTeamName
//         arrDatas[4] : awayTeamAge | arrDatas[5] : awayTeamTotal | arrDatas[6] : awayTeamManner
		// 선택된 away팀의 정보를 찾을 때 사용할 값, 동시에 home팀이 가진 정보
		String homeField = arrDatas[2];
		String homeDate = arrDatas[0];
		String homeTime = arrDatas[1];

		String awayTeamName = arrDatas[3].trim();
		String awayNickName;

		ReservationDTO home = new ReservationDTO(); // 팀장별 사용기록
		ReservationDTO away = new ReservationDTO();
		ArrayList<ReservationVO> homeInfo = null;
		ArrayList<ReservationVO> awayInfo = null;
		ReserveDTO reserve = new ReserveDTO(); // 구장예약현황
		ArrayList<ReserveListDTO> reserveList = new ArrayList<>(); // 도전신청리스트
		MemberDTO memberHome = null; // 회원정보
		MemberDTO memberAway = null;

		ZonedDateTime nowSeoul = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
		String nowDate = nowSeoul.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		// reserve update
		// 홈팀의 구장명, 사용날짜, 사용시간과 어웨이팀이름으로 해당어웨이의 도전신청 조회
		reserveList = rlService.findByFieldAndDateAndTimeAndTeam(homeField, homeDate, homeTime, awayTeamName);
		awayNickName = reserveList.get(0).getNickName();
		// 어웨이팀 정보를 reserve테이블에 추가 및 수정
		reserve = service.findReserve(homeField, homeDate, homeTime);
		reserve.setNickNameB(awayNickName);
		reserve.setNameB(awayTeamName);
		reserve.setDateB(nowDate);
		reserve.setState("B");

		// reservelist update
		// 선택된 어웨이팀의 리스트를 조회해서 같은날짜 같은시간 다른장소 신청제거
		rlService.deleteByDateAndTimeAndNickName(homeDate, homeTime, awayNickName);
		// 모든 사용자를 조회해서 같은날짜 같은시간 같은장소 신청자 제거(선택된 어웨이팀 포함)
		rlService.deleteByFieldAndDateAndTime(homeField, homeDate, homeTime);

		// reservation update : state "매칭 대기중" -> "매칭완료"
		home = rvService.findByid(reserve.getNickNameA());
		away = rvService.findByid(awayNickName);
		homeInfo = home.getInfo();
		awayInfo = away.getInfo();
		for (int i = 0; i < homeInfo.size(); i++) {
			String hField = homeInfo.get(i).getField();
			String hDate = homeInfo.get(i).getDate();
			String hTime = homeInfo.get(i).getTime();

			if (hField.equals(homeField) & hDate.equals(homeDate) & hTime.equals(homeTime)) {
				homeInfo.get(i).setState("매칭 완료");
			}
		}
		for (int i = 0; i < awayInfo.size(); i++) {
			String aField = awayInfo.get(i).getField();
			String aDate = awayInfo.get(i).getDate();
			String aTime = awayInfo.get(i).getTime();

			if (aField.equals(homeField) & aDate.equals(homeDate) & aTime.equals(homeTime)) {
				awayInfo.get(i).setState("매칭 완료");
			}
		}
		// member - hadPoint update
		memberHome = mService.findByid((String) session.getAttribute("userId"));
		memberAway = mService.findByNickName(awayNickName);

		memberHome.setHadPoint(memberHome.getHadPoint() + 50000);
		memberAway.setHadPoint(memberAway.getHadPoint() - 50000);
		mService.save(memberAway);

		// yuki start
		AlarmVO alarm = new AlarmVO();

		alarm.setSender(memberHome.getTName());
		alarm.setReceiver(awayTeamName);
		alarm.setSendDate(nowDate);
		alarm.setIsRead("안읽음");
		alarm.setAlarmType("어웨이");
		alarm.setMessage(homeField + "\n" + homeDate + " " + homeTime + "경기\n홈팀 : " + memberHome.getTName());
		TeamDTO awayteam = tService.findBytName(awayTeamName);
		TeamDTO hometeam = tService.findBytName(memberHome.getTName());

		for (int i = 0; i < awayteam.getTeamMember().size(); i++) {
			MemberDTO awayteamMem = mService.findByNickName(awayteam.getTeamMember().get(i));
			ArrayList<AlarmVO> awayteamMemalarms = new ArrayList<>();
			try {
				awayteamMem.setAlarmCount(awayteamMem.getAlarmCount() + 1);
				awayteamMemalarms = awayteamMem.getAlarms();
				awayteamMemalarms.add(alarm);
			} catch (Exception e) {
				awayteamMem.setReadCount(0);
				awayteamMem.setAlarmCount(1);
				awayteamMemalarms = new ArrayList<>();
				awayteamMemalarms.add(alarm);
			}
//         awayteamMem.setAlarms(awayteamMemalarms);
//         mService.save(awayteamMem);
//         Manner resManner = new Manner();
//         resManner.setUnreadalarmcount(awayteamMem.getAlarmCount()-awayteamMem.getReadCount());
//         resManner.setMessage(homeField + "\n" + homeDate + " " + homeTime + "경기\n홈팀 : " + memberHome.getTName());
//         simpMessagingTemplate.convertAndSend("/queue/away/" + awayteamMem.getNickName(), resManner);
//         log.info("홈팀이 어웨이팀 어셉트 했을때 보내는 도착지와 메세지 : " + "/queue/away/" + awayteamMem.getNickName() + " / " + resManner.getMessage());

//         // Add Tokens
//         if(awayteamMem.getFireBaseToken() != null){
//            tokenList.add(awayteamMem.getFireBaseToken());
//            log.info("토큰토큰토큰토큰 :: " + awayteamMem.getFireBaseToken());
//         }
		}

		// yuki end
//      Firebase firebase = new Firebase();
//      // Try FireBase
//      try {
//         log.info("파베 메세징의 성공 res :: " + firebase.multipleSendToToken(tokenList, "매칭이 완료되었어요!", "상대 팀 : " +  memberHome.getTName() + "\n 홈 >> 예약에서 확인하기"));
//      } catch (FirebaseMessagingException e){
//         log.info("파베 메세징의 실패 :: " + e.getMessage());
//      }


		// save
		tService.save(hometeam);
		service.save(reserve);
		mService.save(memberHome);
		rvService.save(home);
		rvService.save(away);

		return "redirect:/reservation";
	}

	@RequestMapping(value = "/makeMannerAndResultForm", method = RequestMethod.POST)
	@ApiOperation(value = "매너 및 전적 기입 모달생성", response = ReserveDTO.class)
	@ResponseBody
	public ReserveDTO makeMannerAndResult(@RequestParam("date") String date, @RequestParam("rtime") String time, @RequestParam("fieldname") String field,
										  @RequestParam("reserveType") String type) throws Exception {
		log.info("makeMannerAndResultForm 컨트롤러 들어옴,,,,,,,,");
		ReserveDTO reserve = service.findReserve(field, date, time);
		if (type.equals("홈")) { //reserve nameA가 우리팀

		} else { //어웨이 >> reserve nameB가 우리팀
			String myteam = reserve.getNameB();
			String otherteam = reserve.getNameA();
			reserve.setNameA(myteam);
			reserve.setNameB(otherteam);
		}
		log.info("컨트롤러에서 패치로 보낼 값 : " + reserve);
		return reserve;
	}

	@RequestMapping(value = "/submitmannerandresult", method = RequestMethod.POST)
	@ApiOperation(value = "매너 및 전적 기입", response = ReserveDTO.class)
	@ResponseBody
	public void submitMannerAndResult(@RequestParam("field") String field, @RequestParam("date") String date, @RequestParam("time") String time,
									  @RequestParam("myteam") String myteam, @RequestParam("otherteam") String otherteam,
									  @RequestParam("mannerscore") Integer mannerscore, @RequestParam("matchresult") String matchresult) {


		log.info("submitMannerAndResult 컨트롤러 들어옴 ,,, 받은 값 mannerscore, matchresult, field, date ,time, myteam , otherteam: " + mannerscore + " / " + matchresult + " / " + field + " / " + date + " / " + time + " / " + myteam + " / " + otherteam);
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
		ArrayList<ReserveDTO> allreserve = service.findAll();
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

	}

}