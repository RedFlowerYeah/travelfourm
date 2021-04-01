package com.travelfourm.controller;

import com.travelfourm.service.WeatherService;
import com.travelfourm.vo.WeatherResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author 34612
 * @CreateTime 2021/4/1 15:14
 */

@RestController
@RequestMapping("/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @GetMapping("/cityId/{cityId}")
    public WeatherResponseVO getWeatherByCityId(@PathVariable("cityId")String cityId){
        return weatherService.getDataByCityId(cityId);
    }

    @GetMapping("/cityName/{cityName}")
    public WeatherResponseVO getWeatherByCityName(@PathVariable("cityName")String cityName){
        return weatherService.getDataByCityName(cityName);
    }
}
