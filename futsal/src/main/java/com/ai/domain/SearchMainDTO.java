package com.ai.domain;

import java.util.List;

import lombok.Data;

@Data
public class SearchMainDTO {
	List<FieldDTO> NAfields;
	List<TeamDTO> NAteams;
}