package com.travelfourm.config;

import com.travelfourm.quartz.AlphaJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.core.jmx.JobDataMapSupport;
import org.quartz.core.jmx.JobDetailSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;


//配置 -> 数据库 -> 调用
@Configuration
public class QuartzConfig {

    /**
     * Factory实例化的过程
     * 1.通过FactoryBean封装Bean的实例化过程
     * 2.将FactoryBean装配到Spring容器里
     * 3.将FactoryBean注入给其他的Bean
     * 4.该Bean得到的是FactoryBean所管理的对象的实例*/

    //配置JobDetail
    @Bean
    public JobDetailFactoryBean alphaJobDetail(){
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(AlphaJob.class);
        factoryBean.setName("alphaJob");
        factoryBean.setGroup("alphaJobGroup");
        factoryBean.setDurability(true);
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }

    //配置Trigger（SimpleTriggerFactoryBean（简单的）CronTriggerFactoryBean（复杂的））
    @Bean
    public SimpleTriggerFactoryBean alphaTrigger(JobDetail aplhaJobDetail){
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(aplhaJobDetail);
        factoryBean.setName("alphaTrigger");
        factoryBean.setGroup("alphaTriggerGroup");
        factoryBean.setRepeatInterval(3000);
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }


}
