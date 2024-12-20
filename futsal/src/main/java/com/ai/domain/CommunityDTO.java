package com.ai.domain;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

@Data
@Document(collection = "board")
public class CommunityDTO{

    @Transient
    public static final String SEQUENCE_NAME = "board_sequence";

    @Id
    private String id;
    private Integer idx;
    private String writer;
    private String title;
    private String message;
    private String tName;
    private String uniform;
    private String regDate = String.valueOf(ZonedDateTime.now(ZoneId.of("Asia/Seoul"))).replace("T"," ").substring(0,19);
    private String rsvFld;
    private String price;

    private Integer views = 0;
    private ArrayList<CommunityVO> reply;
    private ArrayList<String> reportNickNames;
    private ArrayList<String> reportMessages;

    public void setReply(ArrayList<CommunityVO> reply) {
        this.reply = reply;
    }
}