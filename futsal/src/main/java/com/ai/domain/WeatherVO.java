package com.ai.domain;

import lombok.Data;

@Data
public class WeatherVO {
    public String temp, feels_like, temp_min, temp_max, humidity; // 온도 , 체감온도, 최저 온도, 최고 온도, 습도
    public String main, icon, date; // 날씨, 날씨 아이콘
    public Integer time; // 3시간씩자름
}
