package com.travelfourm.controller;

import com.travelfourm.service.CVOService;
import com.travelfourm.service.City2Service;
import com.travelfourm.service.HsjcDataService;
import com.travelfourm.service.HsjcHospitalService;
import com.travelfourm.vo.CovhsjcVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

/**
 * @Author 34612
 * @CreateTime 2021/4/8 14:22
 */
@RestController
@RequestMapping("/CVO")
public class COV19Controller {

    private Logger logger = LoggerFactory.getLogger(COV19Controller.class);

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private CVOService cvoService;

    @Autowired
    private City2Service city2Service;

    @Autowired
    private HsjcHospitalService hsjcHospitalService;

    @Autowired
    private HsjcDataService hsjcDataService;

    @GetMapping("/city")
    public ModelAndView getCVOCity(Model model) throws Exception{
        model.addAttribute("title","疫情城市一览");
        model.addAttribute("city",cvoService.getCVOCity());
        return new ModelAndView("CVO/city","CVO",model);
    }

    @GetMapping("/hsjc/{cityId}")
    public ModelAndView getReportByCityId(@PathVariable("cityId") String cityId, Model model) throws Exception {
        model.addAttribute("title", "核酸检测机构");
        model.addAttribute("date",new Date());
        model.addAttribute("cityId", cityId);
        model.addAttribute("cityList",city2Service.listCity1());
        model.addAttribute("hospital",hsjcDataService.getDataByCityId(cityId));
        return new ModelAndView("CVO/queryhsjg", "reportModel", model);
    }
}
