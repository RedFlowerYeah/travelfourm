package com.travelfourm.entity;

import lombok.Data;

import java.util.List;

@Data
public class Hits {
    private String datasetName;//违规项目所属数据集名称
    private List<String> words;//违规文本关键字
}
