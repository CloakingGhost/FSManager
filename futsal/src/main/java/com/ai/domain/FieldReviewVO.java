package com.ai.domain;

import lombok.Data;

@Data
public class FieldReviewVO {
    String nickName, review, regDate;
    public FieldReviewVO(String nickName, String review, String regDate){
        this.nickName = nickName;
        this.review = review;
        this.regDate = regDate;
    }
    public FieldReviewVO(){}

}