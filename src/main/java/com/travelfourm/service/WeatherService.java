package com.travelfourm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelfourm.Util.StatusCodeConstant;
import com.travelfourm.vo.WeatherResponseVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @Author 34612
 * @CreateTime 2021/4/1 16:21
 */

@Service
public class WeatherService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);

    private static final String WEATHER_URL = "http://wthrcdn.etouch.cn/weather_mini?";

    @Autowired
    private RestTemplate restTemplate;

    public WeatherResponseVO getDataByCityId(String cityId){
        String url = WEATHER_URL + "citykey=" + cityId;

        return doGetWeather(url,WeatherResponseVO.class);
    }

    public WeatherResponseVO getDataByCityName(String cityName){
        String url = WEATHER_URL + "city=" + cityName;

        return doGetWeather(url, WeatherResponseVO.class);
    }

    private <T> T doGetWeather(String uri, Class<T> type) {
        String key = uri;
        String strBody = null;

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);

        if (StatusCodeConstant.OK == responseEntity.getStatusCodeValue()) {
            strBody = responseEntity.getBody();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        T t = null;

        try {
            t = objectMapper.readValue(strBody, type);
        } catch (Exception e) {
            logger.error("Error!", e);
        }
        return t;
    }
}
