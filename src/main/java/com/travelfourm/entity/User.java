package com.travelfourm.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author 34612
 */
@Data
public class User {
    private int id;

    private String username;

    private String password;

    private String salt;

    private String email;

    private int type;

    private int status;

    private String activationcode;

    private String headerUrl;

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
