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

        /**
         * 这里是以毫秒为单位的计算 1000ms =  1s ，这里也就是5分钟刷新一次*/
        factoryBean.setRepeatInterval(1000 * 60 * 5);
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }


    /***
     * 刷新天气
     */
    @Bean
    public JobDetailFactoryBean weatherRefreshJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(WeatherDataSyncJob.class);
        factoryBean.setName("weatherRefreshJob");
        factoryBean.setGroup("weatherJobGroup");
        factoryBean.setDurability(true);
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }

    @Bean
    public SimpleTriggerFactoryBean weatherRefreshTrigger(JobDetail weatherRefreshJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(weatherRefreshJobDetail);
        factoryBean.setName("weatherRefreshTrigger");
        factoryBean.setGroup("weatherTriggerGroup");

        /**
         * 这里定时任务，60分钟去调一次api接口*/
        factoryBean.setRepeatInterval(1000 * 60 * 60);
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }
}
