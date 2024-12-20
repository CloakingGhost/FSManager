package com.ai.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.ai.domain.ReserveListDTO;
import com.ai.repository.ReserveListRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Repository
public class ReserveListServiceImpl implements ReserveListService {
	@Autowired
	ReserveListRepository repo;
	@Override
	public ReserveListDTO insert(ReserveListDTO rList) {
		// TODO Auto-generated method stub
		return repo.insert(rList);
	}
	@Override
	public ArrayList<ReserveListDTO> findAll() {
		// TODO Auto-generated method stub
		return repo.findAll();
	}
	@Override
	public ArrayList<ReserveListDTO> findByFieldAndDateAndTime(String field, String date, String time) {
		// TODO Auto-generated method stub
		return repo.findByFieldAndDateAndTime(field, date, time);
	}
	@Override
	public void deleteByDateAndTimeAndNickName(String date, String time, String nickName) {
		System.out.println("date : " + date);
		System.out.println("time : " + time);
		System.out.println("nickName : " + nickName);
		repo.deleteByDateAndTimeAndNickName(date, time, nickName);
	}
	@Override
	public void deleteByNickName(String nickName) {
		System.out.println("nickName : " + nickName);
		repo.deleteByNickName(nickName);
	}

	@Override
	public void deleteByFieldAndDateAndTime(String homeField, String homeDate, String homeTime) {
		repo.deleteByFieldAndDateAndTime(homeField, homeDate, homeTime);
	}

	@Override
	public ArrayList<ReserveListDTO> findByFieldAndDateAndTimeAndTeam(String field, String date, String time,
																	  String team) {
		// TODO Auto-generated method stub
		return repo.findByFieldAndDateAndTimeAndTeam(field, date, time, team);
	}
	@Override
	public ArrayList<ReserveListDTO> findByDateAndTimeAndNickName(String date, String time, String nickName) {
		// TODO Auto-generated method stub
		return repo.findByDateAndTimeAndNickName(date, time, nickName);
	}
}