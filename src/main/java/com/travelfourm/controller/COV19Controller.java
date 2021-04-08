package com.travelfourm.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.org.apache.xpath.internal.operations.Mod;
import com.travelfourm.Util.HttpUtil;
import com.travelfourm.service.CVOService;
import com.travelfourm.vo.COVCityVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

/**
 * @Author 34612
 * @CreateTime 2021/4/8 14:22
 */
@RestController
@RequestMapping("/CVO")
public class COV19Controller {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private CVOService cvoService;

    @GetMapping("/city")
    public ModelAndView getCVOCity(Model model) throws Exception{
        model.addAttribute("title","疫情城市一览");
        model.addAttribute("city",cvoService.getCVOCity());
        return new ModelAndView("CVO/city","CVO",model);
    }
}
