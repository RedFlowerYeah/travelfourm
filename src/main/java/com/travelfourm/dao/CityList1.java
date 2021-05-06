package com.travelfourm.dao;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @Author 34612
 * @CreateTime 2021/5/4 10:36
 * 转换格式
 * @XmlRootElement xml文件的根元素
 * @XmlElement xml文件的所要转换的元素
 */
@Data
@XmlRootElement(name = "c")
@XmlAccessorType(XmlAccessType.FIELD)
public class CityList1 {
    @XmlElement(name = "d")
    private List<City1> cityList1;
}
