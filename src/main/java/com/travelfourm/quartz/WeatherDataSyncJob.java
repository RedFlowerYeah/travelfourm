package com.travelfourm.quartz;

import com.travelfourm.dao.City;
import com.travelfourm.service.CityService;
import com.travelfourm.service.WeatherService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.List;

/**
 * @Author 34612
 * @CreateTime 2021/4/1 19:13
 */
@Slf4j
public class WeatherDataSyncJob implements Job {

    @Autowired
    private CityService cityService;

    @Autowired
    private WeatherService weatherDataService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            log.info("开始获取天气！");

            // 获取城市ID列表
            List<City> cityList = null;
            try {
                cityList = cityService.listCity();
            } catch (Exception e) {
                log.error("Exception!", e);
            }

            /**
             * 遍历cityList中的所有城市Id，获取天气信息*/
            for (City city : cityList) {
                String cityId = city.getCityId();
                log.info("城市天气ID：" + cityId);
                weatherDataService.syncDataByCityId(cityId);
            }
            log.info("获取天气结束！");
    }
}