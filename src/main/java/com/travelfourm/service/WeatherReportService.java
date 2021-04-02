package com.travelfourm.service;

import com.travelfourm.vo.WeatherResponseVO;
import com.travelfourm.vo.WeatherVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author 34612
 * @CreateTime 2021/4/2 15:08
 */
@Service
public class WeatherReportService {

    @Autowired
    private WeatherService weatherService;

    public WeatherVO getDataByCityId(String cityId){
        WeatherResponseVO responseVO = weatherService.getDataByCityId(cityId);
        return responseVO.getData();
    }
}
