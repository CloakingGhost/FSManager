package com.ai.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ai.domain.FieldDTO;
import com.ai.domain.MemberDTO;
import com.ai.domain.TeamDTO;
import com.ai.repository.FieldRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FieldServiceImpl implements FieldService {
   @Autowired
   FieldRepository repo;
   
   public FieldDTO findByid(String id) {
      return repo.findByid(id);
   }

   @Override
   public ArrayList<FieldDTO> findAll() {
      return repo.findAll();
   }

   @Override
   public FieldDTO findByfName(String fName) {
      return repo.findByfName(fName);
   }

   @Override
   public ArrayList<FieldDTO> findByFNameRegex(String fName) {
      return repo.findByFNameRegex(fName);
   }

   @Override
   public void save(FieldDTO field) {
      repo.save(field);
   }

   @Override
   public ArrayList<FieldDTO> findByFAddressRegex(String fAddress) {
      return repo.findByFAddressRegex(fAddress);
   }

   @Override
   public ArrayList<FieldDTO> findTop10ByOrderByLikeListCountDesc() {
      return repo.findTop10ByOrderByLikeListCountDesc();
   }

   @Override
   public ArrayList<FieldDTO> findTop12ByOrderByReviewsCountDesc() {
      return repo.findTop12ByOrderByReviewsCountDesc();
   }
   
}