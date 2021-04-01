package com.travelfourm.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author 34612
 * @CreateTime 2021/4/1 15:21
 */

@Data
public class WeatherResponseVO implements Serializable {

    private static final long serialVersionUID = -8483256225271502962L;

    private WeatherVO data;

    private Integer status;

    private String desc;

}
