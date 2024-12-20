package com.ai.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpSession;

import com.ai.domain.*;
import com.ai.service.*;
import com.google.firebase.messaging.FirebaseMessagingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@Api(tags = {"도전신청팀 리스트관련 API"})
@RequestMapping(path = "/reserveListTo")
public class ReserveListController {
	@Autowired
	ReserveService rService;
	@Autowired
	ReserveListService service;
	@Autowired
	MemberService mService;
	@Autowired
	ReservationService rvService;
	@Autowired
	FieldService fService;


	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@ApiOperation(value = "도전 신청", response = ReservationDTO.class)
	@ResponseBody
	public Integer insertReserveList(
			@RequestParam("field") String field, @RequestParam("fTime") String fTime, @RequestParam("fDate") String fDate,
			@RequestParam("nickName2") String nickName2, @RequestParam("tName2") String tName2, @RequestParam("rDate2") String rDate2, HttpSession session) {
		Integer resultNum = 1;
		ReserveDTO reserve = rService.findReserve(field, fDate, fTime);
		MemberDTO homeMember = mService.findByNickName(reserve.getNickNameA());
		String nickName = mService.findByid((String) session.getAttribute("userId")).getNickName();
		ReserveListDTO rList = new ReserveListDTO();
		ReservationVO reservation = new ReservationVO();
		ArrayList<ReservationVO> reservationList = new ArrayList<>();

		try {
			ArrayList<ReservationDTO> rvdtolist = new ArrayList<>();
			String awayteamname1 = mService.findByNickName(nickName).getTName();
			rvdtolist = rvService.findAll();
			int size = rvdtolist.size();
			for (int i = 0; i < size; i++) {
				String allrvoname = rvdtolist.get(i).getId(); //예약했던사람들의 이름
				String awayteamname2 = mService.findByNickName(allrvoname).getTName(); //예약했던사람들의 팀이름
				ReservationDTO awaymemrvdto = rvService.findByid(allrvoname); // 예약했던사람들의 예약목록
				for (int j = 0; j < awaymemrvdto.getInfo().size(); j++) {// 예약했던사람들의 예약목록만큼돌린다
					ReservationVO awaymemrvvo = awaymemrvdto.getInfo().get(j); //예약했던사람들의 예약목록중에 vo객체하나
					if (awaymemrvvo.getField().equals(field) && awaymemrvvo.getDate().equals(fDate) && awaymemrvvo.getTime().equals(fTime) && awayteamname2.equals(awayteamname1)) {
						resultNum = -1;
					}
				}
			}


//         int _size = reservationDTO.getInfo().size()-1;
//         for (int i = _size; i >= 0; i--) {
//            if (tempVo.get(i).getField().equals(field) && tempVo.get(i).getTime().equals(fTime) &&
//                  tempVo.get(i).getDate().equals(fDate) && tempVo.get(i).getType().equals("어웨이") &&
//                  tempVo.get(i).getState().equals("매칭 대기중")) {
//               resultNum = -1;
//               break;
//            }
//         }
			if (resultNum == 1) {
				rList.setField(field);
				rList.setPrice(reserve.getPrice());
				rList.setDate(fDate);
				rList.setTime(fTime);
				rList.setTeam(tName2);
				rList.setNickName(nickName2);
				rList.setRegDate(rDate2);
				service.insert(rList);

				reservation.setDate(rList.getDate());
				reservation.setRegDate(rList.getRegDate());
				reservation.setTime(rList.getTime());
				reservation.setField(rList.getField());
				reservation.setPrice(rList.getPrice());
				reservation.setFieldPic(fService.findByfName(field).getFPic1());
				reservation.setState("매칭 대기중");
				reservation.setType("어웨이");

				reservationList.add(reservation);

				ReservationDTO reservationDTO = rvService.findByid(nickName);
				ArrayList<ReservationVO> tempVo = reservationDTO.getInfo();
				tempVo.add(reservation);
				reservationDTO.setInfo(tempVo);
				rvService.save(reservationDTO);
			}
		} catch (Exception e) {
			rvService.save(new ReservationDTO(nickName, reservationList));
		}
		log.info("패치로 다시 보낼 값 : " + resultNum);

		return resultNum;
	}
}