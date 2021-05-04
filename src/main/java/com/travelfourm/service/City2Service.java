package com.travelfourm.service;

import com.travelfourm.Util.XmlBuilderUtil;
import com.travelfourm.dao.City1;
import com.travelfourm.dao.CityList1;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

/**
 * @Author 34612
 * @CreateTime 2021/5/4 10:30
 */
@Service
public class City2Service {

    public List<City1> listCity1() throws Exception{
        /**
         * 读取citylist.xml文件*/
        Resource resource = new ClassPathResource("citylist2.xml");
        BufferedReader in = new BufferedReader(new InputStreamReader(resource.getInputStream(), "utf-8"));
        StringBuffer buffer = new StringBuffer();
        String line = "";

        while (null != (line = in.readLine())) {

            /**
             * append中的方法中有保护线程安全的关键字:synchronized
             * 所以只会对单行读写，多线程时阻塞相应的线程*/
            buffer.append(line);
        }
        in.close();

        /**
         * xml转换为Object*/
        CityList1 cityList1 = (CityList1) XmlBuilderUtil.xmlStrToObject(buffer.toString(), CityList1.class);
        return cityList1.getCityList1();
    }
}
