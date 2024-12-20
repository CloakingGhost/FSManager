package com.ai.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Data
@Document(collection = "weather")
public class WeatherDTO {
    @Id
    private String id;
    private ArrayList<WeatherVO> weather;
}
