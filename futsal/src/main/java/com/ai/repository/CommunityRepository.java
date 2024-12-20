package com.ai.repository;

import com.ai.domain.CommunityDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.ArrayList;

public interface CommunityRepository extends MongoRepository<CommunityDTO, String> {
    CommunityDTO save(CommunityDTO community);

    CommunityDTO insert(CommunityDTO community);

    ArrayList<CommunityDTO> findAll();

    CommunityDTO findByIdx(Integer targetBoard);

    ArrayList<CommunityDTO> findByWriter(String nickName); // writer == nickName

    ArrayList<CommunityDTO> findBytNameRegex(String word);

    ArrayList<CommunityDTO> findByTitleRegex(String word);

    ArrayList<CommunityDTO> findByWriterRegex(String word);

    void deleteByIdx(Integer idx);

    // Pageable Method
    Page<CommunityDTO> findAll(Pageable pageable);

    Page<CommunityDTO> findByWriter(String nickName, Pageable pageable); // writer == nickName

    Page<CommunityDTO> findBytNameRegex(String word, Pageable pageable);

    Page<CommunityDTO> findByTitleRegex(String word, Pageable pageable);

    Page<CommunityDTO> findByWriterRegex(String word, Pageable pageable);
}