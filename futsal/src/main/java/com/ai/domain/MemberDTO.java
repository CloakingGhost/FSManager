package com.ai.domain;

import java.util.ArrayList;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "member")
public class MemberDTO {
   @Id
   private String id; //카카오구글네이버에서 받아온 이메일값, 프라이머리키
   
   private String name, nickName, sex;
//   private String birthday; // 타입어떻게하지?
   private String phoneNo;
   private String regDate; //가입날짜
   private String platform; //소셜로그인어떤거했는지
   private String tName; //회원가입시는 null, 팀생성할때들어감
   private Integer hadPoint; // 보유포인트, 회원가입시 0
//   private Integer likeListCount=0; // 보유포인트, 회원가입시 0
   private ArrayList<String> likeFieldList;
   //알람시작
   private Integer alarmCount = 0; // 전체 알람 카운트
   private Integer readCount = 0; // 전체알람중에 읽은 알람 카운트
   private ArrayList<AlarmVO> alarms; // 보낸이, 받는이, 메세지내용, 알람보낸시간
   private String fireBaseToken; // FireBase Token
   private ArrayList<String> blockList;

   public MemberDTO() {
      
   }
}