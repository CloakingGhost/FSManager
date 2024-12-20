package com.ai.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Data
@Document(collection = "alarm")
public class AlarmDTO {
    @Id
    private String Id; // nickname
    private Integer alarmCount; // 전체 알람 카운트
    private Integer readCount; // 전체알람중에 읽은 알람 카운트
    private Integer unreadCount; // 전체알람중에 안읽은 알람카운트 => 메인페이지에 종위에 숫자
    private ArrayList<AlarmVO> contents; //

    public AlarmDTO() {}

}
