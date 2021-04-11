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
 * RestTemplate：用于HTTP通信。
 * 如果使用HttpClien进行http通信，不仅代码复杂，还得手动资源回收等。
 * RestTemplate是Spring提供的用于访问Rest服务的客户端，
 * RestTemplate提供了多种便捷访问远程Http服务的方法,能够大大提高客户端的编写效率。
 * 由于两个方法中都是通过RestTemplate进行http通信，并将返回的json数据转换为java对象，
 * 所以，便可将相同的代码进行抽取为一个共同的方法（重构）
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


    /**
     * 方法重构
     * <T> T表示返回值是一个泛型，传递啥，就返回啥类型的数据*/
    private <T> T doGetWeather(String uri, Class<T> type) {
        String key = uri;
        String strBody = null;

        ValueOperations<String ,String> ops = redisTemplate.opsForValue();

        /**
         * 因为第三方接口调用有限制，所以先将数据存储到缓存中*/
        if (redisTemplate.hasKey(key)){
            logger.info("Redis缓存中存在天气预报");
            strBody = ops.get(key);
        }else{
            logger.info("Redis缓存中不存在天气预报，开始调用第三方接口");

            /**
             * 缓存中没有数据，调用第三方api*/
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);

            if (StatusCodeConstant.OK == responseEntity.getStatusCodeValue()) {
                strBody = responseEntity.getBody();
            }

            ops.set(key , strBody , RedisConstant.TIME_OUT, TimeUnit.SECONDS);
        }

        /**
         * 使用了 objectMapper.readValue()方法将json字符串转换为java对象
         * （json字符串中的key要与对象的属性名及类型相对应）*/
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
     * 考虑到接口的并发数问题，先选用部分城市，后续有需要可以接其他城市或其他接口
     */
    private void saveWeatherData(String uri) {
        String key = uri;
        String strBody = null;
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
        if (StatusCodeConstant.OK == responseEntity.getStatusCodeValue()) {
            strBody = responseEntity.getBody();
        }else if (StatusCodeConstant.Fail == responseEntity.getStatusCodeValue()){
            logger.error("调取接口异常，请测试接口返回数据！");
        }
        ops.set(key, strBody, RedisConstant.TIME_OUT, TimeUnit.SECONDS);
    }
}
