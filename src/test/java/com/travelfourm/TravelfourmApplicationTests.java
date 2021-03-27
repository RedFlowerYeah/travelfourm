package com.travelfourm;

import com.travelfourm.dao.AlphaDao;
import com.travelfourm.entity.Comment;
import com.travelfourm.service.AlphaService;
import com.travelfourm.service.CommentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = TravelfourmApplication.class)
public class TravelfourmApplicationTests implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    @Autowired
    private CommentService commentService;
    
    @Test
    public void testApplicationContext(){
        System.out.println(applicationContext);

        AlphaDao alphaDao=applicationContext.getBean(AlphaDao.class);
        System.out.println(alphaDao.select());
    }
//
//    @Test
//    void contextLoads() {
//    }

    @Test
    public void testBeanManager(){
        AlphaService alphaService=applicationContext.getBean(AlphaService.class);
        System.out.println(alphaService);

        alphaService = applicationContext.getBean(AlphaService.class);
        System.out.println(alphaService);
    }

    @Test
    public void testConfig(){
        SimpleDateFormat simpleDateFormat=applicationContext.getBean(SimpleDateFormat.class);

        System.out.println(simpleDateFormat.format(new Date()));
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }

    @Autowired
    private  AlphaDao alphaDao;

    @Autowired
    private SimpleDateFormat simpleDateFormat;

    @Autowired
    private AlphaService alphaService;

    @Test
    public void testDI(){
        System.out.println(alphaService);
        System.out.println(alphaDao);
        System.out.println(simpleDateFormat);
    }

    @Test
    public void testDeleteComment(){
        int id =41;
        commentService.deleteComment(41);
        System.out.println("删除成功");
    }
}
