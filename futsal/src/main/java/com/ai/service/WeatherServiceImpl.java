package com.ai.service;

import com.ai.domain.WeatherDTO;
import com.ai.repository.WeatherRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class WeatherServiceImpl implements WeatherService {
    @Autowired
    WeatherRepository repo;


    @Override
    public WeatherDTO save(WeatherDTO weather) {
        return repo.save(weather);
    }

    @Override
    public WeatherDTO findByid(String id) {
        return repo.findByid(id);
    }
}
