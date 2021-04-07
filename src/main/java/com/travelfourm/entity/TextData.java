package com.travelfourm.entity;

import lombok.Data;

import java.util.List;

/**
 * @author 34612
 */
@Data
public class TextData {

    /**
     * msg 不合规项描述信息
     * hits 命中关键词信息*/
    private String msg;
    private List<Hits> hits;

}
