package com.travelfourm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelfourm.Util.RedisConstant;
import com.travelfourm.Util.StatusCodeConstant;
import com.travelfourm.vo.WeatherResponseVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

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

    /**
     * 增加Redis缓存*/
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 根据id查询天气信息*/
    public WeatherResponseVO getDataByCityId(String cityId){
        String url = WEATHER_URL + "citykey=" + cityId;

        return doGetWeather(url,WeatherResponseVO.class);
    }

    /**
     * 根据城市名查询天气信息*/
    public WeatherResponseVO getDataByCityName(String cityName){
        String url = WEATHER_URL + "city=" + cityName;

        return doGetWeather(url, WeatherResponseVO.class);
    }


    private <T> T doGetWeather(String uri, Class<T> type) {
        String key = uri;
        String strBody = null;

        ValueOperations<String ,String> ops = redisTemplate.opsForValue();

        /**
         * 因为第三方接口调用有限制，所以先将数据存储到缓存中*/
        if (redisTemplate.hasKey(key)){
            logger.info("Redis缓存中存在数据");
            strBody = ops.get(key);
        }else{
            logger.info("Redis缓存中不存在数据");

            /**
             * 缓存中没有数据，调用第三方api*/
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);

            if (StatusCodeConstant.OK == responseEntity.getStatusCodeValue()) {
                strBody = responseEntity.getBody();
            }

            ops.set(key , strBody , RedisConstant.TIME_OUT, TimeUnit.SECONDS);
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

    public void syncDataByCityId(String cityId) {
        String uri = WEATHER_URL + "citykey=" + cityId;
        saveWeatherData(uri);
    }

    /**
     * 把天气数据保存到缓存中
     *
     * @param uri
     */
    private void saveWeatherData(String uri) {
        String key = uri;
        String strBody = null;
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
        if (StatusCodeConstant.OK == responseEntity.getStatusCodeValue()) {
            strBody = responseEntity.getBody();
        }
        ops.set(key, strBody, RedisConstant.TIME_OUT, TimeUnit.SECONDS);
    }
}
