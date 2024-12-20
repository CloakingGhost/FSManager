package com.ai.domain;

import lombok.Data;

import java.util.ArrayList;

@Data
public class AppSearchBoardDTO {
    ArrayList<CommunityDTO> titleSort;
    ArrayList<CommunityDTO> teamSort;
    ArrayList<CommunityDTO> writerSort;
    ArrayList<CommunityDTO> reserveSort;

}