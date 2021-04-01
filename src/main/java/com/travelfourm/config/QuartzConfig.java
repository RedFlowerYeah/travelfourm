package com.travelfourm.config;

import com.travelfourm.quartz.PostScoreRefreshJob;
import com.travelfourm.quartz.WeatherDataSyncJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;


/**配置 -> 数据库 -> 调用
 *@author 34612*/
@Configuration
public class QuartzConfig {
    /***
     * 刷新帖子分数任务
     */
    @Bean
    public JobDetailFactoryBean postScoreRefreshJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(PostScoreRefreshJob.class);
        factoryBean.setName("postScoreRefreshJob");
        factoryBean.setGroup("communityJobGroup");
        factoryBean.setDurability(true);
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }

    @Bean
    public SimpleTriggerFactoryBean postScoreRefreshTrigger(JobDetail postScoreRefreshJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(postScoreRefreshJobDetail);
        factoryBean.setName("postScoreRefreshTrigger");
        factoryBean.setGroup("communityTriggerGroup");
        factoryBean.setRepeatInterval(1000 * 60 * 5);
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }


    /**
     * 频率（多长时间执行一次）
     */
    private static final Integer TIME = 1800;

    @Bean
    public JobDetail weatherDataSyncJobJobDetail() {
        return JobBuilder
                .newJob(WeatherDataSyncJob.class)
                .withIdentity("weatherDataSyncJobJobDetail")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger weatherDataSyncJobTrigger() {
        SimpleScheduleBuilder schedule = SimpleScheduleBuilder
                .simpleSchedule()
                .withIntervalInSeconds(TIME)
                .repeatForever();
        return TriggerBuilder
                .newTrigger()
                .forJob(weatherDataSyncJobJobDetail())
                .withIdentity("weatherDataSyncJobTrigger")
                .withSchedule(schedule)
                .build();
    }
}
