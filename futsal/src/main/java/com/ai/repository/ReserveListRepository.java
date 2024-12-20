package com.ai.repository;

import java.util.ArrayList;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.web.bind.annotation.RequestParam;

import com.ai.domain.ReserveListDTO;

public interface ReserveListRepository extends MongoRepository<ReserveListDTO, String> {
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