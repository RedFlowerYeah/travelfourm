package com.travelfourm.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author 34612
 * @CreateTime 2021/5/4 9:49
 */
@Data
public class HsjcVO implements Serializable {

    private static final long serialVersionUID = 7543970989532137455L;

    private List<HsjcHospitalVO> data;

    private String city_id;

    private String city;

    private String province;
}
