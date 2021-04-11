package com.travelfourm.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author 34612
 * @CreateTime 2021/4/1 16:00
 *
 * @Serializable 因为这些Java对象要通过网络传输，所以，要序列化
 * 网络传输会将对象转换成字节流传输，序列化可以将一个对象转化成一段字符串编码,以便在网络上传输或者做存储处理,使用时再进行反序列
 * 而字符串不用序列化的原因是如果你看过javaSE的源码中，字符串是已经实现了Serializable接口的，所以它已经是序列化了的
 */

@Data
public class YesterdayVO implements Serializable {

    private static final long serialVersionUID = -8410142878644403242L;

    private String date;

    private String high;

    private String fx;

    private String low;

    private String fl;

    private String type;
}
