package com.ai.service;

import java.util.ArrayList;

import com.ai.domain.FieldDTO;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Service;

import com.ai.domain.TeamDTO;

@Service
public interface TeamService {
   public TeamDTO findBytName(String tName);

   public TeamDTO findBy_id(String id);
   public TeamDTO insert(TeamDTO insertTeam);
   public ArrayList<TeamDTO> findAll();

   @Query("{'tName':{'$regex':'?0','$options':'i'}}")
   public ArrayList<TeamDTO> findByTNameRegex(String tName);

   @Query("{'tArea':{'$regex':'?0','$options':'i'}}")
   public ArrayList<TeamDTO> findByTAreaRegex(String tArea);

   public void save(TeamDTO team);

}