package com.ai.service;

import com.ai.domain.WeatherDTO;
import org.springframework.stereotype.Service;

@Service
public interface WeatherService {
    WeatherDTO save(WeatherDTO weather);
    WeatherDTO findByid(String id);
}
