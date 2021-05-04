package com.travelfourm.quartz;

import com.travelfourm.dao.City1;
import com.travelfourm.service.City2Service;
import com.travelfourm.service.HsjcHospitalService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Author 34612
 * @CreateTime 2021/5/4 10:46
 */
@Slf4j
public class HsjcDataSyncJob implements Job {

    @Autowired
    private City2Service city2Service;

    @Autowired
    private HsjcHospitalService hsjcHospitalService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("开始获取核酸检测机构！");

        // 获取城市ID列表
        List<City1> cityList1 = null;
        try {
            cityList1 = city2Service.listCity1();
        } catch (Exception e) {
            log.error("Exception!", e);
        }

        /**
         * 遍历cityList中的所有城市Id，获取核酸检测机构信息*/
        for (City1 city1 : cityList1) {
            String cityId = city1.getCityId();
            log.info("城市ID：" + cityId);
            hsjcHospitalService.syncDataByCityId(cityId);
        }
        log.info("获取核酸检测机构结束！");
    }
}
