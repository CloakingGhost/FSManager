package com.ai.service;

import com.ai.domain.FieldReviewDTO;
import com.ai.repository.FieldReviewRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;


@Slf4j
@Component
@Repository
public class FieldReviewServiceImpl implements FieldReviewService {

    @Autowired
    FieldReviewRepository repo;

    @Override
    public FieldReviewDTO findByid(String id) {
        return repo.findByid(id);
    }

    @Override
    public ArrayList<FieldReviewDTO> findAll() {
        log.info("예약 정보 전부 조회");
        return repo.findAll();
    }

    @Override
    public void insert(FieldReviewDTO FieldReview) {
        repo.insert(FieldReview);
    }

    @Override
    public void save(FieldReviewDTO FieldReview) {
        repo.save(FieldReview);
    }

    @Override
    public FieldReviewDTO findByIdAndReviews(FieldReviewDTO FieldReview) {
        return repo.findByIdAndReviews(FieldReview);
    }


}