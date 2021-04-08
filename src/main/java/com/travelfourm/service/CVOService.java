package com.travelfourm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelfourm.Util.StatusCodeConstant;
import com.travelfourm.vo.COVCityVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @Author 34612
 * @CreateTime 2021/4/8 15:48
 */
@Slf4j
@Service
public class CVOService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String CVO_URL = "http://apis.juhe.cn/springTravel/risk?";

    String key = "306c95c88704936d00d1c17284a93f7b";

    public COVCityVO getCVOCity(){
        String url = CVO_URL + "key=" + key;

        return doGetCVOCity(url,COVCityVO.class);
    }

    private <T> T doGetCVOCity(String url,Class<T> type){
        String key = url;
        String strBody = null;
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        if (StatusCodeConstant.OK == responseEntity.getStatusCodeValue()) {
            strBody = responseEntity.getBody();
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
}
