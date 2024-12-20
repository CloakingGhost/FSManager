package com.ai.controller;

import com.ai.domain.FieldDTO;
import com.ai.domain.OpenWeather;
import com.ai.domain.WeatherDTO;

import com.ai.domain.WeatherVO;
import com.ai.service.FieldService;
import com.ai.service.WeatherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.util.ArrayList;

@Slf4j
@Controller
public class WeatherController {

    @Autowired
    WeatherService service;

    @Autowired
    FieldService fService;

    private final String apiKey = "35d78855684a0cd284467304f3438609";
    private final String apiKey2 = "270dc6b06153afd2767ea188cecc35f5";
    private final String BASE_URL = "https://api.openweathermap.org/data/2.5/forecast";



    // TEST
    @RequestMapping(value ="/TEST")
    public void TEST() throws Exception {
        ArrayList<FieldDTO> fList = fService.findAll();
        for (int i = 0; i < 750; i++) {
            System.out.println(i + " 번째 실행");
            StringBuilder urlBuilder = new StringBuilder(BASE_URL);
            try {
                urlBuilder.append("?" + URLEncoder.encode("lat", "UTF-8") + "=" + fList.get(i).getLatitude());
                urlBuilder.append("&" + URLEncoder.encode("lon", "UTF-8") + "=" + fList.get(i).getLongitude());
                urlBuilder.append("&" + URLEncoder.encode("appid", "UTF-8") + "=" + apiKey);
                urlBuilder.append("&" + URLEncoder.encode("lang", "UTF-8") + "=kr");
                urlBuilder.append("&" + URLEncoder.encode("units", "UTF-8") + "=metric");
            } catch (Exception e) {
                log.info(e.getMessage());
            }
            WeatherDTO dto = new WeatherDTO();
            ArrayList<WeatherVO> voList = new ArrayList<WeatherVO>();
            dto.setId(fList.get(i).getFName());
            RestTemplate template = new RestTemplate();
            OpenWeather res = template.getForObject(urlBuilder.toString(), OpenWeather.class);
            for (int j = 0; j < res.getList().size(); j++) {

                WeatherVO vo = new WeatherVO();
                vo.setTemp(String.format("%.1f", res.getList().get(j).getMain().getTemp()));
                vo.setFeels_like(String.format("%.1f", res.getList().get(j).getMain().getFeels_like()));
                vo.setTemp_min(String.format("%.1f",res.getList().get(j).getMain().getTemp_min()));
                vo.setTemp_max(String.format("%.1f",res.getList().get(j).getMain().getTemp_max()));
                vo.setHumidity(String.format("%.0f",res.getList().get(j).getMain().getHumidity()));
                vo.setDate(res.getList().get(j).getDt_txt().substring(0, 10));
                vo.setTime(Integer.parseInt(res.getList().get(j).getDt_txt().substring(11, 13)));
                if (res.getList().get(j).getWeather().get(0).getMain().equalsIgnoreCase("Rain")) {
                    vo.setMain("비");
                    vo.setIcon("https://i.ibb.co/cLL0jV9/rain.png");
                }
                if (res.getList().get(j).getWeather().get(0).getMain().equalsIgnoreCase("Clouds")) {
                    vo.setMain("흐림");
                    vo.setIcon("https://i.ibb.co/HBLLyjJ/cloud.png");
                }
                if (res.getList().get(j).getWeather().get(0).getMain().equalsIgnoreCase("Clear")) {
                    vo.setMain("맑음");
                    vo.setIcon("https://i.ibb.co/1zwbP9R/sunny.png");
                }
                if (res.getList().get(j).getWeather().get(0).getMain().equalsIgnoreCase("Snow")) {
                    vo.setMain("눈");
                    vo.setIcon("https://i.ibb.co/61f6pTP/snow.png");
                }
                voList.add(vo);
            }
            dto.setWeather(voList);
            service.save(dto);
            if (i != 0 && i % 50 == 0) {
                Thread.sleep(60000);
            }
        }
    }

    // TEST
    @RequestMapping(value ="/TEST2")
    public void TEST2() throws Exception{
        ArrayList<FieldDTO> fList = fService.findAll();
        for(int i = 750; i <= 1560; i++){
            System.out.println(i + " 번째 실행");
            StringBuilder urlBuilder = new StringBuilder(BASE_URL);
            try {
                urlBuilder.append("?" + URLEncoder.encode("lat", "UTF-8") + "=" + fList.get(i).getLatitude());
                urlBuilder.append("&" + URLEncoder.encode("lon", "UTF-8") + "=" + fList.get(i).getLongitude());
                urlBuilder.append("&" + URLEncoder.encode("appid", "UTF-8") + "=" + apiKey2);
                urlBuilder.append("&" + URLEncoder.encode("lang", "UTF-8") + "=kr");
                urlBuilder.append("&" + URLEncoder.encode("units", "UTF-8") + "=metric");
            } catch (Exception e){
                log.info(e.getMessage());
            }
            WeatherDTO dto = new WeatherDTO();
            ArrayList<WeatherVO> voList = new ArrayList<WeatherVO>();
            dto.setId(fList.get(i).getFName());
            RestTemplate template = new RestTemplate();
            OpenWeather res = template.getForObject(urlBuilder.toString(), OpenWeather.class);
            for(int j = 0; j < res.getList().size(); j++){
                WeatherVO vo = new WeatherVO();
                vo.setTemp(String.format("%.1f", res.getList().get(j).getMain().getTemp()));
                vo.setFeels_like(String.format("%.1f", res.getList().get(j).getMain().getFeels_like()));
                vo.setTemp_min(String.format("%.1f",res.getList().get(j).getMain().getTemp_min()));
                vo.setTemp_max(String.format("%.1f",res.getList().get(j).getMain().getTemp_max()));
                vo.setHumidity(String.format("%.0f",res.getList().get(j).getMain().getHumidity()));
                vo.setDate(res.getList().get(j).getDt_txt().substring(0, 10));
                vo.setTime(Integer.parseInt(res.getList().get(j).getDt_txt().substring(11, 13)));
                if(res.getList().get(j).getWeather().get(0).getMain().equalsIgnoreCase("Rain")){
                    vo.setMain("비");
                    vo.setIcon("https://i.ibb.co/cLL0jV9/rain.png");
                }
                if(res.getList().get(j).getWeather().get(0).getMain().equalsIgnoreCase("Clouds")){
                    vo.setMain("흐림");
                    vo.setIcon("https://i.ibb.co/HBLLyjJ/cloud.png");
                }
                if(res.getList().get(j).getWeather().get(0).getMain().equalsIgnoreCase("Clear")){
                    vo.setMain("맑음");
                    vo.setIcon("https://i.ibb.co/1zwbP9R/sunny.png");
                }
                if(res.getList().get(j).getWeather().get(0).getMain().equalsIgnoreCase("Snow")){
                    vo.setMain("눈");
                    vo.setIcon("https://i.ibb.co/61f6pTP/snow.png");
                }
                voList.add(vo);
            }
            dto.setWeather(voList);
            service.save(dto);
            if(i != 750 && i % 50 == 0){
                Thread.sleep(60000);
            }
        }

        // TEST CODE RESET
        /*
        for(int i = 0; i < fList.size(); i++){
            WeatherDTO dto = new WeatherDTO();
            dto.setId(fList.get(i).getFName());
            dto.setWeather(new ArrayList<WeatherVO>());
            service.save(dto);
        }

         */
    }
}