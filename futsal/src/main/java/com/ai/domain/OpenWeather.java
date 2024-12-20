package com.ai.domain;

import lombok.Data;

import java.util.List;


@Data
public class OpenWeather {

    private int cod;
    private String message;
    private int cnt;
    private List<list> List;

    @Data
    public static class list {
        // 데이터 시간
        private long dt;
        private Main main;
        private List<Weather> weather;
        private Clouds clouds;
        private Wind wind;
        // 가시성
        private int visibility;
        // 모름
        private double pop;
        private Sys sys;
        private String dt_txt;
    }

    @Data
    public static class Main {
        // 온도
        private float temp;
        // 체감 온도
        private float feels_like;
        // 최저 온도
        private float temp_min;
        // 최고 온도
        private float temp_max;
        // 대기압
        private int pressure;
        // 해수면 대기압
        private float sea_level;
        // 지면 대기압
        private float grnd_level;
        // 습도
        private float humidity;
        // 뭔지 모름
        private float temp_kf;
    }

    @Data
    public static class Weather {
        // 기상 조건
        private int id;
        // 날씨
        private String main;
        // 날씨 정보
        private String description;
        // 날씨 아이콘
        private String icon;
    }

    @Data
    public static class Clouds {
        // 흐림
        private int all;
    }
    @Data
    public static class Wind {
        // 풍속
        private float speed;
        // 풍향
        private int deg;
        // 돌풍
        private float gust;
    }

    @Data
    public static class Sys {
        // 모름
        private String pod;
    }
}
