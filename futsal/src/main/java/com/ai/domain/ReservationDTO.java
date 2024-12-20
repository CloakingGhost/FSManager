package com.ai.domain;

import java.util.ArrayList;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "reservation")
public class ReservationDTO {
	@Id
	String id;
	ArrayList<ReservationVO> info;
	public ReservationDTO() {}
	public ReservationDTO(String id, ArrayList<ReservationVO> info) {
		this.id = id;
		this.info = info;
	}
}
