package com.travelfourm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelfourm.Util.RedisConstant;
import com.travelfourm.Util.StatusCodeConstant;
import com.travelfourm.vo.CovhsjcVO;
import lombok.extern.slf4j.Slf4j;
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
 * @CreateTime 2021/5/4 9:55
 */
@Slf4j
@Service
public class HsjcHospitalService {

    private static final Logger logger = LoggerFactory.getLogger(HsjcHospitalService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String CVO_HSJC = "http://apis.juhe.cn/springTravel/hsjg?";

    String key = "306c95c88704936d00d1c17284a93f7b";

    public CovhsjcVO getCovHsjc(String cityId){
        String url = CVO_HSJC + "key=" + key + "&city_id=" + cityId;

        return doGetCovhsjc(url,CovhsjcVO.class);
    }

    private <T> T doGetCovhsjc(String url,Class<T> type){
        String key = url;
        String strBody = null;

        ValueOperations<String ,String> ops = redisTemplate.opsForValue();

        /**
         * 先从缓存中查询数据*/
        if (redisTemplate.hasKey(key)){
            log.info("Redis中存在缓存核酸检测机构信息");
            strBody = ops.get(key);
        }else {
            log.info("Redis缓存中不存在缓存核酸检测机构信息，开始调用第三方接口");

            /**
             * 缓存中不存在数据，开始调用第三方接口*/
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
            if (StatusCodeConstant.OK == responseEntity.getStatusCodeValue()) {
                strBody = responseEntity.getBody();
            }

            /**
             * 写入Redis
             * 这里目前保存的时间是1小时，不清楚修改之后保存会不会有报错，先留着*/
            ops.set(key,strBody, RedisConstant.TIME_OUT, TimeUnit.SECONDS);
        }


        ObjectMapper objectMapper = new ObjectMapper();
        T t = null;
        try {
            t = objectMapper.readValue(strBody, type);
        } catch (Exception e) {
            log.error("Error!", e);
        }
        return t;
    }

    public void syncDataByCityId(String cityId) {
        String uri = CVO_HSJC + "key=" + key + "&city_id=" + cityId;
        saveHsjc(uri);
    }

    /**
     * 把天气数据保存到缓存中
     *
     * @param uri
     * 考虑到接口的并发数问题，先选用部分城市，后续有需要可以接其他城市或其他接口
     */
    private void saveHsjc(String uri) {
        String key = uri;
        String strBody = null;
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
        if (StatusCodeConstant.OK == responseEntity.getStatusCodeValue()) {
            strBody = responseEntity.getBody();
        }else if (StatusCodeConstant.Fail == responseEntity.getStatusCodeValue()){
            logger.error("调取接口异常，请查看接口返回数据是否正常！");
        }
        ops.set(key, strBody, RedisConstant.TIME_OUT, TimeUnit.SECONDS);
    }
}
