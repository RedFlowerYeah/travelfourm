package com.travelfourm.service;

import com.travelfourm.dao.ProvinceMapper;
import com.travelfourm.entity.Province;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author 34612
 * @CreateTime 2021/4/16 13:10
 */
@Service
public class ProvinceService {

    private static final Logger logger = LoggerFactory.getLogger(ProvinceService.class);

    @Autowired
    private ProvinceMapper provinceMapper;

    public List<Province> findProvince(){
        return provinceMapper.selectProvince();
    }
}
