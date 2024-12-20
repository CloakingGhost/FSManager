package com.ai.repository;


import java.util.ArrayList;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ai.domain.ReservationDTO;

public interface ReservationRepository extends MongoRepository<ReservationDTO, String> {
	 ArrayList<ReservationDTO> findAll();
	 ReservationDTO findByid(String id);
	 ReservationDTO insert(ReservationDTO reservation);
	 ReservationDTO save(ReservationDTO reservation);
}
