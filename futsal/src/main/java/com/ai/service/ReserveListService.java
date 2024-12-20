package com.ai.service;

import java.util.ArrayList;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.ai.domain.ReserveListDTO;

@Service
public interface ReserveListService {
	ReserveListDTO insert(ReserveListDTO rList);

	ArrayList<ReserveListDTO> findAll();
	ArrayList<ReserveListDTO> findByFieldAndDateAndTime(@RequestParam("field") String field,
														@RequestParam("date") String date, @RequestParam("time") String time);
	ArrayList<ReserveListDTO> findByFieldAndDateAndTimeAndTeam(@RequestParam("field") String field,
															   @RequestParam("date") String date, @RequestParam("time") String time, @RequestParam("team") String team);
	ArrayList<ReserveListDTO> findByDateAndTimeAndNickName(@RequestParam("date") String date,
														   @RequestParam("time") String time, @RequestParam("nickName") String nickName);
	void deleteByDateAndTimeAndNickName(@RequestParam("date") String date, @RequestParam("time") String time,
										@RequestParam("nickName") String nickName);
	void deleteByNickName(@RequestParam("nickName") String nickName);

	void deleteByFieldAndDateAndTime(@RequestParam("field") String field, @RequestParam("date") String date, @RequestParam("time") String time);
}