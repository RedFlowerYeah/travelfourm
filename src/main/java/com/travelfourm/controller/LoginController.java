package com.travelfourm.controller;

import com.google.code.kaptcha.Producer;
import com.travelfourm.Util.CommunityConstant;
import com.travelfourm.Util.CommunityUtil;
import com.travelfourm.Util.RedisKeyUtil;
import com.travelfourm.entity.User;
import com.travelfourm.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author 34612
 */

@Controller
public class LoginController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptcharProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    /**跳转到注册页面*/
    @GetMapping("/register")
    public String getRegisterPage() {
        return "/site/register";
    }

    /**跳转到登录页面*/
    @GetMapping("/login")
    public String getLoginPage(){
        return "/site/login";
    }

    /**注册方法*/
    @PostMapping("/register")
    public String register(Model model, User user){
        Map<String,Object> map=userService.register(user);
        if (map == null || map.isEmpty()){
            model.addAttribute("msg","注册成功,我们已经向您的邮箱发送了一封激活邮件,请尽快激活!");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }else {
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return  "/site/register";
        }
    }

    /**
     * 激活账号方式
     * http://localhost:8080/community/activation/101/code*/
    @GetMapping("/activation/{userId}/{code}")
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code) {
        int result = userService.activation(userId, code);
        if (result == ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "激活成功,您的账号已经可以正常使用了!");
            model.addAttribute("target", "/login");
        } else if (result == ACTIVATION_REPEAT) {
            model.addAttribute("msg", "无效操作,该账号已经激活过了!");
            model.addAttribute("target", "/index");
        } else {
            model.addAttribute("msg", "激活失败,您提供的激活码不正确!");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }

    /**验证码功能*/
    @GetMapping("/kaptcha")
    public void getKaptcha(HttpServletResponse response, HttpSession session){
        /**
         * 创建文本以及以图片的形式创建验证码*/
        String text=kaptcharProducer.createText();
        BufferedImage image=kaptcharProducer.createImage(text);

        /**
         * 将验证码存入session中*/
        String kaptchaOwner = CommunityUtil.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner",kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        response.addCookie(cookie);

        /**
         * 将验证码存入redis中*/
        String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(redisKey,text,60, TimeUnit.SECONDS);

        /**
         * 服务器发送给浏览器的类型*/
        response.setContentType("image/png");
        try {

            /**
             * 将图片输出*/
            OutputStream outputStream=response.getOutputStream();
            ImageIO.write(image,"png",outputStream);
        }catch (IOException e){
            logger.error("获取验证码失败："+e.getMessage());
        }
    }

    /**
     * 登录验证
     * @CookieValue 用来获取浏览器中存放验证码的Cookie */
    @PostMapping("/login")
    public String login(String username,String password,String code,boolean rememberme,
                        Model model/*HttpSession httpSession*/,HttpServletResponse response,
                        @CookieValue("kaptchaOwner")String kaptchaOwner) {

        /**
         * 首先判断用户是否填写了表单中的验证码，
         * 如果右则去判断是否等于存放在Redis中的验证码
         * 如果为空，且验证码不正确或过期等情况，则提示验证码不正确*/
        String kaptcha = null;
        if (StringUtils.isNoneBlank(kaptchaOwner)){
            String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(redisKey);
        }
        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)) {
            model.addAttribute("codeMsg", "验证码不正确!");
            return "/site/login";
        }

        /**
         * 判断账号密码是否和当前登录的对象一致*/
        int expiredSeconds = rememberme ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String,Object> map=userService.login(username,password,expiredSeconds);
        if (map.containsKey("ticket")){

            /**
             * 获取Cookie中的对象值*/
            Cookie cookie=new Cookie("ticket",map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        }else {
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/login";
        }
    }

    /**登出论坛网站*/
    @GetMapping("/logout")
    public String logout(@CookieValue("ticket")String ticket){
        userService.logout(ticket);
        SecurityContextHolder.clearContext();
        return "redirect:/login";
    }
}
