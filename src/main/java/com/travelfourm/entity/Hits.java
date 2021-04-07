package com.travelfourm.entity;

import lombok.Data;

import java.util.List;

/**
 * @author 34612
 */
@Data
public class Hits {

    /**
     * 违规项目所属数据集名称*/
    private String datasetName;


    /**
     * 违规文本关键字*/
    private List<String> words;

}
