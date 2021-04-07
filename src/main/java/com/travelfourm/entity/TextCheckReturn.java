package com.travelfourm.entity;

import lombok.Data;

import java.util.List;

/**
 * @author 34612
 */
@Data
public class TextCheckReturn {

    /**
     * log_id 请求唯一id，用于问题定位
     * conclusion 审核结果，可取值：合规、不合规、疑似、审核失败
     * conclusionType 审核结果类型，可取值1.合规，2.不合规，3.疑似，4.审核失败
     * data 不合规/疑似/命中白名单项详细信息*/
    private long log_id;

    private String conclusion;

    private Integer conclusionType;

    private List<TextData> data;
}
