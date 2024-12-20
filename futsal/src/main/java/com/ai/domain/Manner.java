package com.ai.domain;

import lombok.Data;

@Data
public class Manner {

    private String nickName, field, date, time, nameA, nameB, hometeam, message, type;
    private Integer unreadalarmcount;

    public Manner() {
    }

    public Manner(String nickName, String field, String date, String time){
        this.nickName = nickName;
        this.date = date;
        this.field = field;
        this.time = time;
    }
    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }

}