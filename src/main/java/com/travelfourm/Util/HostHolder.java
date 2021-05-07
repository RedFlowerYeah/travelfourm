package com.travelfourm.Util;

import com.travelfourm.entity.User;
import org.springframework.stereotype.Component;

/**
 * 持有用户信息，从session中获取对象信息
 * @author 34612*/
@Component
public class HostHolder {

    /**
     * ThreadLocal<Object></Object>
     * 以线程为map的key-value类型 存取值（get、set）->ThreadLocalMap
     * 一个ThreadLocal只能持有一个ThreadLocalMap（保证线程安全）
     * ThreadLocal会为使用的变量提供一个副本，当多线程访问同一个资源时，操作的是本地内存中的变量
     * */
    private ThreadLocal<User> users=new ThreadLocal<>();

    /**
     * set为设置key-value*/
    public void setUser(User user) {
        users.set(user);
    }

    /**
     * get为获取key-value*/
    public User getUser(){
        return users.get();
    }

    /**
     * remove为移除变量*/
    public void clear(){
        users.remove();
    }
}
