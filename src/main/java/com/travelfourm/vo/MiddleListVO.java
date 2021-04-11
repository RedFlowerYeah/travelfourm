package com.travelfourm.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author 34612
 * @CreateTime 2021/4/8 15:46
 */
@Data
public class MiddleListVO implements Serializable {

    private static final long serialVersionUID = 3582172346901189663L;

    private String type;

    private String province;

    private String city;

    private String county;

    private String area_name;

    private List communitys;

    private String county_code;
}
