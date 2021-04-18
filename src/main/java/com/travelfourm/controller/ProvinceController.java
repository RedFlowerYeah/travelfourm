package com.travelfourm.controller;

import com.travelfourm.entity.Province;
import com.travelfourm.service.ProvinceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
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
}
