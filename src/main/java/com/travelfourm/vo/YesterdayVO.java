package com.travelfourm.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author 34612
 * @CreateTime 2021/4/1 16:00
 *
 * @Serializable 因为这些Java对象要通过网络传输，所以，要序列化
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
