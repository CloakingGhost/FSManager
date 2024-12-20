package com.ai.repository;

import com.ai.domain.FieldDTO;
import com.ai.domain.FieldReviewDTO;
import com.ai.domain.ReservationDTO;
import com.ai.domain.ReserveListDTO;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.ArrayList;

public interface FieldReviewRepository extends MongoRepository<FieldReviewDTO, String> {
    ArrayList<FieldReviewDTO> findAll();
    FieldReviewDTO findByid(String id);
    FieldReviewDTO insert(FieldReviewDTO FieldReview);
    FieldReviewDTO save(FieldReviewDTO FieldReview);
    FieldReviewDTO findByIdAndReviews(FieldReviewDTO FieldReview);
}