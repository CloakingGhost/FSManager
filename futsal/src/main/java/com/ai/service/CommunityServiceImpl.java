package com.ai.service;

import com.ai.domain.CommunityDTO;
import com.ai.repository.CommunityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Slf4j
@Component
@Repository
public class CommunityServiceImpl implements CommunityService {
    @Autowired
    CommunityRepository repo;

    @Override
    public ArrayList<CommunityDTO> findAll() { return repo.findAll(); }

    @Override
    public void save(CommunityDTO community) { repo.save(community); }

    @Override
    public void insert(CommunityDTO community) { repo.insert(community); }

    @Override
    public void deleteByIdx(Integer idx) { repo.deleteByIdx(idx); }

    @Override
    public CommunityDTO findByIdx(Integer targetBoard) { return repo.findByIdx(targetBoard); }

    @Override // nickName == writer
    public ArrayList<CommunityDTO> findByWriter(String nickName) { return repo.findByWriter(nickName); }

    @Override
    public ArrayList<CommunityDTO> findByTitleRegex(String word) { return repo.findByTitleRegex(word); }

    @Override
    public ArrayList<CommunityDTO> findBytNameRegex(String word) { return repo.findBytNameRegex(word); }

    @Override
    public ArrayList<CommunityDTO> findByWriterRegex(String word) { return repo.findByWriterRegex(word); }

    @Override
    public Page<CommunityDTO> findAll(Pageable pageable) { return repo.findAll(pageable); }

    @Override
    public Page<CommunityDTO> findByWriter(String nickName, Pageable pageable) { return repo.findByWriter(nickName, pageable); }

    @Override
    public Page<CommunityDTO> findBytNameRegex(String word, Pageable pageable) { return repo.findBytNameRegex(word, pageable); }

    @Override
    public Page<CommunityDTO> findByTitleRegex(String word, Pageable pageable) { return repo.findByTitleRegex(word, pageable); }

    @Override
    public Page<CommunityDTO> findByWriterRegex(String word, Pageable pageable) { return repo.findByWriterRegex(word, pageable); }
}