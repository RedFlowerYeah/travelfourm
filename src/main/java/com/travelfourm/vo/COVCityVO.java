package com.travelfourm.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author 34612
 * @CreateTime 2021/4/8 15:35
 */
@Data
public class COVCityVO implements Serializable {

    private static final long serialVersionUID = -103770135947094416L;

    private Integer error_code;

    private String reason;

    private ResultVO result;
}
