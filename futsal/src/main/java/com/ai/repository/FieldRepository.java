package com.ai.repository;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ai.domain.FieldDTO;
import com.ai.domain.MemberDTO;
import com.ai.domain.TeamDTO;

public interface FieldRepository extends MongoRepository<FieldDTO, String> {
   FieldDTO findByid(@RequestParam("id") String id);
   ArrayList<FieldDTO> findAll();
   FieldDTO findByfName(@RequestParam("fName") String fName);

   @Query("{'fName':{'$regex':'?0','$options':'i'}}")
   ArrayList<FieldDTO> findByFNameRegex(String fName);
   
   FieldDTO save(FieldDTO field);
   @Query("{'fAddress':{'$regex':'?0','$options':'i'}}")
   ArrayList<FieldDTO> findByFAddressRegex(String fAddress);

   ArrayList<FieldDTO> findTop10ByOrderByLikeListCountDesc();

   ArrayList<FieldDTO> findTop12ByOrderByReviewsCountDesc();

}