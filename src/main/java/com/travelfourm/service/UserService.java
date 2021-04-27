package com.travelfourm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.travelfourm.Util.CommunityUtil;
import com.travelfourm.Util.MailClient;
import com.travelfourm.Util.RedisKeyUtil;
import com.travelfourm.dao.UserMapper;
import com.travelfourm.entity.LoginTicket;
import com.travelfourm.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.travelfourm.Util.CommunityConstant.*;

/**
 * @author 34612
 */

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private RedisTemplate redisTemplate;


    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextpath;

    /**查找全部用户信息*/
    public List<User> findAllUser(){
        return userMapper.selectAllUser();
    }

    /**
     * 通过id查找用户信息*/
    public User findUserById(int id){
        User user = getCache(id);
        if (user == null){
            user = initCache(id);
        }
        return user;
    }

    /**
     * 判断是否通过邮箱激活账号*/
    public int activation(int userId, String code) {
        User user=userMapper.selectById(userId);

        /**
         * if status == 1 则为表示已经注册过的状态
         * if status == 0 则进行注册*/
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationcode().equals(code)) {
            userMapper.updateStatus(userId,1);
            clearCache(userId);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }

    /**
     * 用户注册*/
    public Map<String,Object>register(User user){
        Map<String,Object> map=new HashMap<>();

        //空值处理
        if (user == null){
            throw new IllegalArgumentException("参数不能为空！");
        }
        if (StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","账号不能为空！");
        }
        if (StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空！");
        }
        if (StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空!");
        }
        User user1=userMapper.selectByName(user.getUsername());
        //验证账号是否存在
        if (user1 != null){
            map.put("usernameMsg","账号已存在");
        }
        // 验证邮箱
        user1 = userMapper.selectByEmail(user.getEmail());
        if (user1 != null) {
            map.put("emailMsg", "该邮箱已被注册!");
            return map;
        }

        //注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationcode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // 激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());

        // http://localhost:8080/travelfourm/activation/101/code
        String url = domain  + contextpath + "/activation/" + user.getId() + "/" + user.getActivationcode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);

        return map;
    }

    /**
     * 用户登录*/
    public Map<String,Object> login(String username,String password,int expiredSeconds){
        Map<String,Object> map=new HashMap<>();

        //空值处理
        if (StringUtils.isBlank(username)){
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)){
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        //验证账号
        User user=userMapper.selectByName(username);
        if (user == null){
            map.put("usernameMsg","该账号不存在");
            return map;
        }

        //验证状态
        if (user.getStatus()==0){
            map.put("usernameMsg","该账号未激活");
            return map;
        }

        //验证密码
        password = CommunityUtil.md5(password+user.getSalt());
        if (!user.getPassword().equals(password)){
            map.put("passwordMsg","密码不正确");
            return map;
        }

        //生成登录凭证
        LoginTicket loginTicket=new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));

        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey,loginTicket);

        map.put("ticket",loginTicket.getTicket());
        return map;
    }

    /**
     * 用户登出*/
    public void logout(String ticket){
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey,loginTicket);
    }

    /**
     * 统计用户*/
    public int countUser(){
        return userMapper.selectCountUser();
    }

    /**登录凭证
     * 后期可以废弃掉*/
    public LoginTicket findLoginTicket(String ticket){
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }

    /**
     * 更新用户头像*/
    public int updateHeader(int userId, String headerUrl) {
        int rows = userMapper.updateHeader(userId, headerUrl);

        /**
         * 清楚缓存*/
        clearCache(userId);

        return rows;
    }

    /**
     * 更新用户信息*/
    public int updateUserInfo(User userInfo){
        int result = userMapper.updateUserInfo(userInfo);
        int userId = userInfo.getId();

        /**
         * 清除缓存*/
        clearCache(userId);

        return result;
    }

    /**通过username查找相关User信息*/
    public User findUserByName(String username){

        return userMapper.selectByName(username);
    }

    /**1.优先从缓存中读取数据*/
    private User getCache(int userId){
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }

    /**2.取不到时，初始化缓存数据*/
    private User initCache(int userId){
        User user = userMapper.selectById(userId);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey,user,3600, TimeUnit.SECONDS);
        return user;
    }

    /**3.数据变更时，清除缓存数据*/
    private void clearCache(int userId){
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }

    /**权限控制*/
    public Collection<? extends GrantedAuthority> getAuthorities(int userId){
        User  user = this.findUserById(userId);

        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {
                switch (user.getType()){
                    case 1:
                        return AUTHORITY_ADMIN;
                    case 2:
                        return AUTHORITY_MODERATOR;
                        default:
                            return AUTHORITY_USER;
                }
            }
        });
        return list;
    }
}
