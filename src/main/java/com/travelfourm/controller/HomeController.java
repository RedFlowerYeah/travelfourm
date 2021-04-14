package com.travelfourm.controller;

import com.travelfourm.Util.CommunityConstant;
import com.travelfourm.entity.DiscussPost;
import com.travelfourm.entity.Page;
import com.travelfourm.entity.User;
import com.travelfourm.service.DiscussPostService;
import com.travelfourm.service.LikeService;
import com.travelfourm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 34612
 */

@Controller
public class HomeController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @GetMapping("/index")
    public String getIndexPage(Model model, Page page,
                               @RequestParam(name = "orderMode",defaultValue = "0")int orderMode){

        //方法调用，SpringMVC会自动实例化Modal和Page，并将这两个注入到Modal中
        //所以，在thymeleaf中可以直接访问Page对象中的数据
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index?orderMode="+orderMode);

        List<DiscussPost> list=discussPostService.findDiscussPosts(0,page.getOffset(),page.getLimit(),orderMode);
        List<Map<String,Object>> discussPosts=new ArrayList<>();
        if (list!=null){
            for (DiscussPost post: list){
                Map<String,Object> map=new HashMap<>();
                map.put("post",post);

                User user=userService.findUserById(post.getUserId());
                map.put("user",user);

                long likeCount  = likeService.findEntityLikeCount(ENTITY_TYPE_POST,post.getId());
                map.put("likeCount", likeCount);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("orderMode",orderMode);
        return "/index";
    }

    /**报错*/
    @GetMapping("/denied")
    public String getDeniedPage(){
        return "/error/404";
    }

    /**跳转到后台页面*/
    @GetMapping("/showUser")
    public String getShowUser(){
        return "/backup/showUser";
    }

    @GetMapping("/queryCVO")
    public String queryCVO(){
        return "/CVO/queryhsjg";
    }
}
