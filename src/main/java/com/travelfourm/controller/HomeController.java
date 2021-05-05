package com.travelfourm.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.qiniu.util.Json;
import com.travelfourm.Util.CommunityConstant;
import com.travelfourm.Util.CommunityUtil;
import com.travelfourm.entity.DiscussPost;
import com.travelfourm.entity.Page;
import com.travelfourm.entity.Province;
import com.travelfourm.entity.User;
import com.travelfourm.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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

    @Autowired
    private CommentService commentService;

    @Autowired
    private ProvinceService provinceService;

    /**
     * 主页*/
    @GetMapping("/index")
    public String getIndexPage(Model model, Page page,
                               @RequestParam(name = "orderMode",defaultValue = "0")int orderMode){

        //方法调用，SpringMVC会自动实例化Modal和Page，并将这两个注入到Modal中
        //所以，在thymeleaf中可以直接访问Page对象中的数据
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index?orderMode="+orderMode);

        List<DiscussPost> list = discussPostService.findDiscussPosts(0,page.getOffset(),page.getLimit(),orderMode);
        List<DiscussPost> list1 = discussPostService.findDiscussHot(10000);
        List<Province> list2 = provinceService.findProvince();

        List<Map<String,Object>> discussPosts = new ArrayList<>();
        List<Map<String,Object>> discussPosts1 = new ArrayList<>();
        List<Map<String,Object>> provinces = new ArrayList<>();

        if (list!=null){
            for (DiscussPost post: list){
                Map<String,Object> map = new HashMap<>();
                map.put("post",post);

                User user = userService.findUserById(post.getUserId());
                map.put("user",user);

                long likeCount  = likeService.findEntityLikeCount(ENTITY_TYPE_POST,post.getId());
                map.put("likeCount", likeCount);
                discussPosts.add(map);
            }
        }

        if (list1 != null){
            for (DiscussPost post1 : list1){
                Map<String ,Object> map1 = new HashMap<>();
                map1.put("post1",post1);

                User user = userService.findUserById(post1.getUserId());
                map1.put("user",user);

                discussPosts1.add(map1);
            }
        }

        if (list2 != null){
            for (Province province : list2){
                Map<String,Object> map2 = new HashMap<>();
                map2.put("province",province);

                provinces.add(map2);
            }
        }

        model.addAttribute("provinces",provinces);
        model.addAttribute("discussPosts1",discussPosts1);
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

    /**
     * 查询疫情城市*/
    @GetMapping("/queryCVO")
    public String queryCVO(){
        return "/CVO/queryhsjg";
    }

    /**
     * echarts表返回数据*/
    @GetMapping("/echarts")
    @ResponseBody
    public List<Integer> getJuhe(){

        /***
         * 将统计数据用list来存储
         */
        int countUser = userService.countUser();
        int countDiscussPosts = discussPostService.countDiscussPosts();
        int countComment = commentService.countComment();
        int countProvinces = provinceService.countProvinces();

        /**
         * 从list中取出对应数据的统计*/
        List<Integer> count = new ArrayList<>();
        count.add(countUser);
        count.add(countDiscussPosts);
        count.add(countComment);
        count.add(countProvinces);

        return count;
    }
}
