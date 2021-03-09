package com.travelfourm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class TravelfourmApplication {

    @PostConstruct
    public void init() {
        //解决netty启动冲突 Netty4Utils
        //为了解决与消息队列中的netty启动冲突
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }

    public static void main(String[] args) {
        SpringApplication.run(TravelfourmApplication.class, args);
    }

}
