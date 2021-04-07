package com.travelfourm.controller;

import com.travelfourm.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
public class DataController {

    @Autowired
    private DataService dataService;


    /**
     * 跳转到统计页面*/
    @RequestMapping(path = "/data",method = {RequestMethod.GET,RequestMethod.POST})
    public String getDataPage(){
        return "/site/admin/data";
    }


    /**
     * 统计UV
     * UV : 网站独立访客（通过ip地址访问）*/
    @PostMapping("/data/uv")
    public String getUV(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date end, Model model){
        if (start.after(end)){
            return "/error/404";
        }else {
            long uv = dataService.calculateUV(start, end);
            model.addAttribute("uvResult", uv);
            model.addAttribute("uvStartDate", start);
            model.addAttribute("uvEndDate", end);

            return "forward:/data";
        }
    }


    /**
     * 统计网站日活跃用户*/
    @PostMapping("/data/dau")
    public String getDAU(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                         @DateTimeFormat(pattern = "yyyy-MM-dd") Date end, Model model){

        if (start.after(end)) {
            return "/error/404";
        }else {
            long dau = dataService.calculateDAU(start, end);

            model.addAttribute("dauResult", dau);
            model.addAttribute("dauStartDate", start);
            model.addAttribute("dauEndDate", end);

            return "forward:/data";
        }
    }
}
