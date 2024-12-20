package com.ai.service;

import com.ai.domain.FieldReviewDTO;
import com.ai.domain.ReservationDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public interface FieldReviewService {
    public ArrayList<FieldReviewDTO> findAll();
    public FieldReviewDTO findByid(String id);
    void insert(FieldReviewDTO FieldReview);
    void save(FieldReviewDTO FieldReview);
    FieldReviewDTO findByIdAndReviews(FieldReviewDTO FieldReview);
}