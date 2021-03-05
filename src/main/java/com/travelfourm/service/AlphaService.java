package com.travelfourm.service;

import com.travelfourm.dao.AlphaDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
//@Scope("prototype")     //这个注解的意义应该是保持原子的一致性，销毁后还能重新调用一次新的方法
public class AlphaService {

    private static final Logger logger = LoggerFactory.getLogger(AlphaService.class);
//    @Autowired
//    private AlphaDao alphaDao;
//
//    public void AlphaServece(){
//        System.out.println("实例化AlphaService");
//    }
//
//    @PostConstruct
//    public void init(){
//        System.out.println("初始化AlphaService");
//    }
//
//    @PreDestroy
//    public void destroy(){
//        System.out.println("销毁AlphaService");
//    }
//
//    public String find(){
//        return alphaDao.select();
//    }

    // 让该方法在多线程环境下,被异步的调用.
    @Async
    public void execute1() {
        logger.debug("execute1");
    }

    /*@Scheduled(initialDelay = 10000, fixedRate = 1000)*/
    public void execute2() {
        logger.debug("execute2");
    }
}
