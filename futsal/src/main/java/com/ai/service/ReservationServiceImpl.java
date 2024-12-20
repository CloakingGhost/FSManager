package com.ai.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.ai.domain.ReservationDTO;
import com.ai.repository.ReservationRepository;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
@Repository
public class ReservationServiceImpl implements ReservationService {

	@Autowired
	ReservationRepository repo;
	
	@Override
	public ReservationDTO findByid(String id) {
		return repo.findByid(id);
	}

	@Override
	public ArrayList<ReservationDTO> findAll() {
		return repo.findAll();
	}

	@Override
	public void insert(ReservationDTO reservation) {
		repo.insert(reservation);
	}

	@Override
	public void save(ReservationDTO reservation) {
		repo.save(reservation);
	}
	
}
