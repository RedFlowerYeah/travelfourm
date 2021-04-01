package com.travelfourm.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author 34612
 * @CreateTime 2021/4/1 16:00
 */

@Data
public class YesterdayVO implements Serializable {

    private static final long serialVersionUID = -806309024676977591L;

    private String date;

    private String high;

    private String fx;

    private String low;

    private String fl;

    private String type;
}
