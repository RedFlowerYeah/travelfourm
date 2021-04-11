package com.travelfourm.Util;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * 百度固定的Auth获取方式
 * @author 34612*/
public class AuthService {

    /**
     * 获取权限token
     * 返回实例对象*/
    public static String getAuth(){

        //apiKey
        String clientId = "p0EAqilifQgdspilxHPVvDU4";

        //secretKey
        String clientSecret = "tFWmVZZO4sRWghG4tveFNn7nM59TxBYL";

        return getAuth(clientId,clientSecret);
    }

    public static String getAuth(String ak,String sk){
        //获取token地址
        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        String getAccessTokenUrl = authHost
                // 1. grant_type为固定参数
                + "grant_type=client_credentials"
                // 2. 官网获取的 API Key
                + "&client_id=" + ak
                // 3. 官网获取的 Secret Key
                + "&client_secret=" + sk;
        try {
            URL realUrl = new URL(getAccessTokenUrl);

            //打开和url之间的连接
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            //获取所有响应头字段
            Map<String , List<String>> map = connection.getHeaderFields();

            //遍历所有的响应头字段
            for (String key : map.keySet()){
                System.err.println(key + "- - >" + map.get(key));
            }

            //定义BufferReader输入流来获取url响应
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result = "";
            String line;
            while ((line = in.readLine()) != null){
                result += line;
            }

            //返回结果实例
            System.err.println("result:" + result);
            JSONObject jsonObject = new JSONObject(result);
            String access_token = jsonObject.getString("access_token");
            return access_token;
        } catch (Exception e) {
            System.err.println("获取token失败!");
            e.printStackTrace(System.err);
        }
        return null;
    }
}
