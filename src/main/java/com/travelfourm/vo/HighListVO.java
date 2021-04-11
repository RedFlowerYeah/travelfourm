package com.travelfourm.vo;

import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author 34612
 * @CreateTime 2021/4/8 15:37
 */
@Data
public class HighListVO implements Serializable {

    private static final long serialVersionUID = 8065779234752198809L;

    private String type;

    private String province;

    private String city;

    private String county;

    private String area_name;

    private List communitys;

    private String county_code;
}
