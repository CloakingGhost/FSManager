package com.ai.domain;

import lombok.Data;

@Data
public class AppCommunityDTO {
    private String[] rsvFld;
    private Integer fldPrc;
    private TeamDTO team;
    private CommunityDTO community;
}