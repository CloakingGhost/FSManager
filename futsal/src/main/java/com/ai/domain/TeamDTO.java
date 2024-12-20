package com.ai.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;


@Data
@Document(collection = "team")
public class TeamDTO {
   @Id
   private String _id; // 캡틴닉네임
   private String tName, uniform, tAge;
   private String tArea, TeamInfo;
   private String tName2, fName, rTime, foundingDate;
   private int tTotal, tManner;
   private Integer tMannercnt =0;
   private String logoPath;
   private ArrayList<String> teamMember;
   private ArrayList<String> applyMember;
   private ArrayList<NoticeVO> notices;
   private ArrayList<RecordVO> records;

   public TeamDTO() {}
}