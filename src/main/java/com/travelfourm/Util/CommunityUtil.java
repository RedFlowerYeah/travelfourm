package com.travelfourm.Util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author 34612
 */
public class CommunityUtil {

    /**
     * 生成随机字符串*/
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    /**
     * MD5加密
     * hello-->abcdedf213*/
    public static String md5(String key){
        if(StringUtils.isBlank(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    /**
     * 方法由上往下继承*/
    /**
     * Map<String,Object> map是一个接口类，包含一个key对象和一个value对象，key-value一一对应
     * 主要找到了对应的key值，就可以找到对应的value值 */
    public static String getJsonString(int code, String msg, Map<String,Object> map){

        /**
         * 直接构建一个JSONObject对象*/
        JSONObject jsonObject =new JSONObject();

        /**
         * 通过put将数据写入*/
        jsonObject.put("code",code);
        jsonObject.put("msg",msg);
        if (map != null){
            /**
             * keySet（）就是获取map中key的名称*/

            for (String key : map.keySet()){

                /**
                 * 将key-value值放入对应的jsonObject对象中*/
                jsonObject.put(key,map.get(key));
            }
        }
        return jsonObject.toJSONString();
    }

    public static String getJsonString(int code,String msg){
        return getJsonString(code,msg,null);
    }

    public static String getJsonString(int code){
        return getJsonString(code,null,null);
    }
}
