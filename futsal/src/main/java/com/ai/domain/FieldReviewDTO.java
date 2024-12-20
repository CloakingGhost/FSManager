package com.ai.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Data
@Document(collection = "fieldreview")
public class FieldReviewDTO {
    @Id
    private String id; // 해당구장명
    private ArrayList<FieldReviewVO> reviews; //리뷰들을 담고있음 ( 각 리뷰의 작성자, 리뷰내용, 작성날짜 )

    public FieldReviewDTO() {}

    public FieldReviewDTO(String id, ArrayList<FieldReviewVO> reviews) {
        this.id = id;
        this.reviews = reviews;
    }
}