package com.travelfourm.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author 34612
 * @CreateTime 2021/5/4 9:43
 */
@Data
public class CovhsjcVO implements Serializable {

    private static final long serialVersionUID = -5470502182697527886L;

    private HsjcVO result;

    private String reason;

    private Integer error_code;
}
