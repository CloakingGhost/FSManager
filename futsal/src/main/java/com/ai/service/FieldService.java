package com.ai.service;

import java.util.ArrayList;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Service;

import com.ai.domain.FieldDTO;
import com.ai.domain.TeamDTO;

@Service
public interface FieldService {
   public FieldDTO findByid(String id);
   public ArrayList<FieldDTO> findAll();
   public FieldDTO findByfName(String fName);
   
   @Query("{'fName':{'$regex':'?0','$options':'i'}}")
   public ArrayList<FieldDTO> findByFNameRegex(String fName);
   
   public void save(FieldDTO field);
   
   @Query("{'fAddress':{'$regex':'?0','$options':'i'}}")
   public ArrayList<FieldDTO> findByFAddressRegex(String fAddress);

   public ArrayList<FieldDTO> findTop10ByOrderByLikeListCountDesc();

   public ArrayList<FieldDTO> findTop12ByOrderByReviewsCountDesc();
}