package com.travelfourm.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author 34612
 * @CreateTime 2021/4/8 15:40
 */
@Data
public class ResultVO implements Serializable {

    private static final long serialVersionUID = 2632027054460545728L;

    private String updated_date;

    private String high_count;

    private String middle_count;

    private List<HighListVO> high_list;

    private List<MiddleListVO> middle_list;
}
