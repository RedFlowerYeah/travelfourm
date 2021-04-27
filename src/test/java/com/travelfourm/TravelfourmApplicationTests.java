package com.travelfourm;

import com.travelfourm.Util.HostHolder;
import com.travelfourm.dao.AlphaDao;
import com.travelfourm.entity.Comment;
import com.travelfourm.entity.DiscussPost;
import com.travelfourm.entity.Province;
import com.travelfourm.entity.User;
import com.travelfourm.service.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = TravelfourmApplication.class)
public class TravelfourmApplicationTests implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(TravelfourmApplication.class);
    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private DiscussPostService discussPostService;
    
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

    @Test
    public void addDiscussPost(){

        int userId = 166;
        String title = "这都行？";
        String content = "这都行？";
        String modular = "广州";

        DiscussPost post = new DiscussPost();
        post.setUserId(userId);
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        post.setModular(modular);

        discussPostService.addDiscussPost(post);

        System.out.println("插入成功");
    }

    @Test
    public void searchDiscussPostByModular(){

        String modular = "广州";

        List<DiscussPost> modular1 = discussPostService.findDiscussPostByModular(modular);

        logger.info(String.valueOf(modular1));
    }

    @Autowired
    private ProvinceService provinceService;

    @Test
    public void searchProvince(){

        List<Province> province = provinceService.findProvince();

        logger.info(String.valueOf(province));
    }

    @Test
    public void getHot(){
        int limit = 3;

       List<DiscussPost> discussPosts = discussPostService.findDiscussHot(3);

        logger.info(String.valueOf(discussPosts));
    }

    @Test
    public void getCountUser(){
        int count = userService.countUser();
        logger.info(String.valueOf(count));
    }
}
