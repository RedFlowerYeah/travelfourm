package com.travelfourm.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author 34612
 * @CreateTime 2021/5/4 9:53
 */
@Data
public class HsjcHospitalVO implements Serializable {

    private static final long serialVersionUID = -3511725114040285649L;

    private String city_id;

    private String name;

    private String province;

    private String city;

    private String phone;

    private String address;
}
