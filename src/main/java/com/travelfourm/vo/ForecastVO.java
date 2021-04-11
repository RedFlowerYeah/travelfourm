package com.travelfourm.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author 34612
 * @CreateTime 2021/4/1 15:37
 */

@Data
public class ForecastVO implements Serializable {

    private static final long serialVersionUID = 2335082727627496029L;

    private String date;

    private String high;

    private String fengli;

    private String low;

    private String fengxiang;

    private String type;
}
