package com.travelfourm;

import com.travelfourm.dao.DiscussPostMapper;
import com.travelfourm.dao.LoginTicketMapper;
import com.travelfourm.dao.MessageMapper;
import com.travelfourm.dao.UserMapper;
import com.travelfourm.entity.LoginTicket;
import com.travelfourm.entity.Message;
import com.travelfourm.entity.User;
import com.travelfourm.service.DiscussPostService;
import com.travelfourm.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = TravelfourmApplication.class)
public class MapperTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Test
    public void selectUserById(){
        User user=userMapper.selectById(1);
        System.out.println(user);


        user=userMapper.selectByName("SYSTEM");
        System.out.println(user);

        user=userMapper.selectByEmail("346125735@qq.com");
        System.out.println(user);
    }

    @Test
    public void testInsertUser(){
        User user=new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("huwenhong@qq.com");
        user.setHeaderUrl("https://www.baidu.com/101.png");
        user.setCreateTime(new Date());

        int rows=userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    @Test
    public void testUpdateUser(){
        int rows=userMapper.updateStatus(1,0);
        System.out.println(rows);

//        rows=userMapper.updatePassword(2,"goudongxi");
//        System.out.println(rows);

        rows=userMapper.updateHeader(1,"http://www.xiurenwang.com/1.png");
        System.out.println(rows);
    }


//    @Test
//    public void testSelectPosts(){
//        List<DiscussPost> list=discussPostMapper.selectDiscussPosts(0,0,10);
//
//        for (DiscussPost post:list){
//            System.out.println(post);
//        }
//
//        int rows=discussPostMapper.selectDiscussPostRows(0);
//        System.out.println(rows);
//    }

    @Test
    public void testInsertLoginTicket(){
        LoginTicket loginTicket=new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));

        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelectLoginTicket(){
        LoginTicket loginTicket=loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);

        loginTicketMapper.updateStatus("abc",1);
        loginTicket=loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);
    }

    @Test
    public void testSelectLetters(){
        List<Message> list = messageMapper.selectConversations(111,0,20);
        for (Message message : list){
            System.out.println(message);
        }

        int count = messageMapper.selectConversationCount(111);
        System.out.println(count);

        messageMapper.selectLetters("111_112",0,10);
        for (Message message:list){
            System.out.println(message);
        }

        count = messageMapper.selectLetterCount("111_112");
        System.out.println(count);

        count = messageMapper.selectLetterUnreadCount(131,"111_113");
        System.out.println(count);
    }

}
