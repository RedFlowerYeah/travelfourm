package com.travelfourm.quartz;

import com.travelfourm.service.CVOService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author 34612
 * @CreateTime 2021/4/9 9:42
 */
@Slf4j
public class CVODataSyncJob implements Job {

    @Autowired
    private CVOService cvoService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        log.info("开始获取疫情城市");

        cvoService.getCVOCity();

        log.info("获取疫情城市信息结束");
    }
}
