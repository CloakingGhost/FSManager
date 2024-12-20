package com.ai.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Data
public class CommunityVO {

    @Transient
    public static final String SEQUENCE_NAME = "board_sequence_vo";

    private Integer idx;
    private String writer;
    private String message;
    private String regDate = String.valueOf(ZonedDateTime.now(ZoneId.of("Asia/Seoul"))).replace("T"," ").substring(0,19);
//    private Integer state = 0;
}