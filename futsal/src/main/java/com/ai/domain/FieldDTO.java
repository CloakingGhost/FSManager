package com.ai.domain;

import java.util.ArrayList;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;



@Data
@Document(collection = "field")
public class FieldDTO {
   @Id
   private String id;
   private String fAddress, fName, fPhoneNo, fPic1, fPic2, fPic3, fPic4, fSize, capacity, fInfo;
   private Integer fPrice1, fPrice2;
   private String shoesRent, uniformRent, ballRent, shower, parking, coldHot;
   private String fRule, fRefundRule, fChangeRule, fWeatherRefundRule, fRefund, fChange;
   private String review, latitude, longitude, reserve;
   private ArrayList<String> likeList;
   private ArrayList<FieldReviewVO> reviews; //리뷰들을 담고있음 ( 각 리뷰의 작성자, 리뷰내용, 작성날짜 )
   private Integer likeListCount;
   private Integer reviewsCount;

   public FieldDTO() {}
}