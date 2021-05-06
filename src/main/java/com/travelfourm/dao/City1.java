package com.travelfourm.dao;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @Author 34612
 * @CreateTime 2021/5/4 10:36
 * 对接xml文件的citylist2
 */
@Data
@XmlRootElement(name = "d")
@XmlAccessorType(XmlAccessType.FIELD)
public class City1 {
    @XmlAttribute(name = "d1")
    private String cityId;
    @XmlAttribute(name = "d2")
    private String cityName;
}
