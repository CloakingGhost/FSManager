package com.ai.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;

@Data
public class AppFieldDTO {
    ArrayList<WeatherVO> weatherList;
    LinkedHashMap<String, String> timeMap;
    FieldDTO field;
}