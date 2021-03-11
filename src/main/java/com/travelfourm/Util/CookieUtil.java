package com.travelfourm.Util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CookieUtil {

    public static String getValue(HttpServletRequest request,String name){
        if (request == null || name == null){
            throw new IllegalArgumentException("参数为空！");
        }

        /**
         * 读Cookie
         * HttpServletRequest中getCookies方法返回了一个Cookie数组，它包含了HTTP请求中的所有Cookie*/
        Cookie[] cookies=request.getCookies();
        if (cookies != null){
            /**
             * 如果cookies数组不为空，则遍历此数组*/
            for (Cookie cookie:cookies){
                /**
                 * 如果此时请求的cookie等于所保存的cookie，则可以直接获取对应的值*/
                if (cookie.getName().equals(name)){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
