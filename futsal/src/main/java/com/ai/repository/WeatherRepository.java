package com.ai.repository;


import com.ai.domain.WeatherDTO;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WeatherRepository extends MongoRepository<WeatherDTO, String> {
    WeatherDTO save(WeatherDTO weather);
    WeatherDTO findByid(String id);
}
