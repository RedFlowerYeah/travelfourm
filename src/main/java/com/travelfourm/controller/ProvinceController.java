package com.travelfourm.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.travelfourm.Util.CommunityUtil;
import com.travelfourm.entity.Province;
import com.travelfourm.service.ProvinceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author 34612
 * @CreateTime 2021/4/16 14:21
 */
@Controller
public class ProvinceController {

    @Autowired
    private ProvinceService provinceService;

    @GetMapping("/getProvince")
    @ResponseBody
    public Map<String, Object> getProvince(){

        Map<String,Object> map = new HashMap<>();
        List<Province> provinces = provinceService.findProvince();

       map.put("data",provinces);
        return map;
    }

    @GetMapping("/showLayui3")
    public String showAllProvinces(){
        return "/backup/showAllProvinces";
    }



    /**
     * 板块信息列表*/
    @GetMapping("/showAllProvinces")
    @ResponseBody
    public Map<String,Object> showAllProvinces1(@RequestParam(required = false,defaultValue = "0")String type,
                                                @RequestParam(required = false,defaultValue = "")String content,
                                                @RequestParam(required = false,defaultValue = "1")int page,
                                                @RequestParam(required = false,defaultValue = "10")int limit){
        /**
         * 开始分页*/
        PageHelper.offsetPage(page,limit);

        /**
         * 分页查询*/
        List<Province> list = new ArrayList<>();

        if (type.equals("0")){
            list = provinceService.findProvince();
        }

        /**
         * 封装数据返回前端*/
        PageInfo pageInfo = new PageInfo(list);
        Map<String ,Object> map = new HashMap<>();
        map.put("code",0);
        map.put("msg","查询成功");
        map.put("count",pageInfo.getTotal());
        map.put("data",pageInfo.getList());
        return map;
    }

    /**
     * 增加板块*/
    @PostMapping("/saveProvince")
    @ResponseBody
    public String insertProvince(String provinceName){
        Province province = new Province();
        province.setProvinceName(provinceName);

        provinceService.addProvince(province);
        return CommunityUtil.getJsonString(0,"添加成功");
    }
}
