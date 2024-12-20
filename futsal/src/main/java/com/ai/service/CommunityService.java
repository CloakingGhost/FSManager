package com.ai.service;

import com.ai.domain.CommunityDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public interface CommunityService {
    public ArrayList<CommunityDTO> findAll();

    public void save(CommunityDTO community);

    public void insert(CommunityDTO community);

    public void deleteByIdx(Integer idx);

    public CommunityDTO findByIdx(Integer targetBoard);

    public ArrayList<CommunityDTO> findByWriter(String nickName);

    public ArrayList<CommunityDTO> findBytNameRegex(String word);

    public ArrayList<CommunityDTO> findByTitleRegex(String word);

    public ArrayList<CommunityDTO> findByWriterRegex(String word);

    //페이징
    public Page<CommunityDTO> findAll(Pageable pageable);

    public Page<CommunityDTO> findByWriter(String nickName, Pageable pageable);

    public Page<CommunityDTO> findBytNameRegex(String word, Pageable pageable);

    public Page<CommunityDTO> findByTitleRegex(String word, Pageable pageable);

    public Page<CommunityDTO> findByWriterRegex(String word, Pageable pageable);

}