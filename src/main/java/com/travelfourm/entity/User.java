package com.travelfourm.entity;

import lombok.Data;

import java.util.Date;

@Data
public class User {
    //id
    private int id;

    //用户名
    private String username;

    //密码
    private String password;

    //盐值
    private String salt;

    //邮箱
    private String email;

    //类型
    private int type;

    //状态
    private int status;

    //验证码
    private String activationcode;

    //头url
    private String headerUrl;

    //创建日期
    private Date createTime;

    @Override
    public String  toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", salt='" + salt + '\'' +
                ", email='" + email + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", activationcode='" + activationcode + '\'' +
                ", headerUrl='" + headerUrl + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
