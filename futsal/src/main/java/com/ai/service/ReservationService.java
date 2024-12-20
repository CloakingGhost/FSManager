package com.ai.service;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

import com.ai.domain.ReservationDTO;

@Service
public interface ReservationService {
	ArrayList<ReservationDTO> findAll();
	ReservationDTO findByid(String id);
	void insert(ReservationDTO reservation);
	void save(ReservationDTO reservation);
}
