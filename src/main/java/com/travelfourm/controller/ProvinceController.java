package com.travelfourm.controller;

import com.travelfourm.entity.Province;
import com.travelfourm.service.ProvinceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public List<Province> getProvince(){
        List<Province> provinces = provinceService.findProvince();
        return provinces;
    }
}
