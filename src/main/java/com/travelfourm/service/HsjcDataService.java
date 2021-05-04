package com.travelfourm.service;

import com.travelfourm.vo.CovhsjcVO;
import com.travelfourm.vo.HsjcHospitalVO;
import com.travelfourm.vo.HsjcVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author 34612
 * @CreateTime 2021/5/4 11:22
 */
@Service
public class HsjcDataService {

    @Autowired
    private HsjcHospitalService hsjcHospitalService;

    public List<HsjcHospitalVO> getDataByCityId(String cityId){
        CovhsjcVO covhsjcVO = hsjcHospitalService.getCovHsjc(cityId);

        HsjcVO hsjcVO = covhsjcVO.getResult();

        List<HsjcHospitalVO> hospitalVOS = hsjcVO.getData();

        return hospitalVOS;
    }
}
