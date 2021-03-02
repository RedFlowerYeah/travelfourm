package com.travelfourm.service;

import com.travelfourm.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
//@Scope("prototype")     //这个注解的意义应该是保持原子的一致性，销毁后还能重新调用一次新的方法
public class AlphaService {

    @Autowired
    private AlphaDao alphaDao;

    public void AlphaServece(){
        System.out.println("实例化AlphaService");
    }

    @PostConstruct
    public void init(){
        System.out.println("初始化AlphaService");
    }

    @PreDestroy
    public void destroy(){
        System.out.println("销毁AlphaService");
    }

    public String find(){
        return alphaDao.select();
    }
}
