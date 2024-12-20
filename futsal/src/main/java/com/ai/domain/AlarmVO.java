package com.ai.domain;

import lombok.Data;

@Data
public class AlarmVO {
    private String sender; // 알람을 보낸사람
    private String receiver; // 알람받는사람 == AlarmDTO에 Id
    private String message; // 해당알람의 메세지
    private String sendDate; // 알람보낸시간
    private String isRead = "안읽음"; // 읽음, 안읽음표시
    private String alarmType; // 알람타입 표시 -> 매너, 홈, 어웨이, ...,

}
