package com.travelfourm.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author 34612
 */
@Data
public class Comment {

    private int id;

    private int userId;

    private int entityType;

    private int entityId;

    private int targetId;

    private String content;

    private int status;

    private Date createTime;

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", userId=" + userId +
                ", entityType=" + entityType +
                ", entityId=" + entityId +
                ", targetId=" + targetId +
                ", content='" + content + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                '}';
    }
}
