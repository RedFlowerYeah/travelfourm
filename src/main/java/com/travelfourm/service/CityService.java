package com.travelfourm.service;

import com.travelfourm.Util.XmlBuilderUtil;
import com.travelfourm.dao.City;
import com.travelfourm.dao.CityList;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

/**
 * @Author 34612
 * @CreateTime 2021/4/1 19:42
 */

@Service
public class CityService {

    public List<City> listCity() throws Exception {
        // 读取XML文件
        Resource resource = new ClassPathResource("citylist.xml");
        BufferedReader in = new BufferedReader(new InputStreamReader(resource.getInputStream(), "utf-8"));
        StringBuffer buffer = new StringBuffer();
        String line = "";
        while (null != (line = in.readLine())) {
            buffer.append(line);
        }
        in.close();
        // XML转换为对象
        CityList cityList = (CityList) XmlBuilderUtil.xmlStrToObject(buffer.toString(), CityList.class);
        return cityList.getCityList();
    }
}
