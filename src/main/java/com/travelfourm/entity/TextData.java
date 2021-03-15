package com.travelfourm.entity;

import lombok.Data;

import java.util.List;

@Data
public class TextData {
    private String msg;//不合规项描述信息
    private List<Hits> hits;//命中关键词信息
}
