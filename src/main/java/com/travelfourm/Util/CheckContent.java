package com.travelfourm.Util;


import com.alibaba.fastjson.JSON;
import com.travelfourm.Util.AuthService;
import com.travelfourm.Util.HttpUtil;
import com.travelfourm.config.BaiduSensitiveConfig;
import com.travelfourm.entity.ReturnStatusEnum;
import com.travelfourm.entity.TextCheckReturn;

import java.net.URLEncoder;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author 34612
 */

public class CheckContent {
    public static Map<String, Object> checkText(String text) {
        Map<String, Object> map = new TreeMap<String, Object>();

        //获取access_token
        String access_token = AuthService.getAuth();
        try {
            //设置请求的编码
            String param = "text=" + URLEncoder.encode(text, "UTF-8");
            //调用文本审核接口并取得结果
            String result = HttpUtil.post(BaiduSensitiveConfig.CHECK_TEXT_URL, access_token, param);

            // JSON解析对象
            TextCheckReturn tcr = JSON.parseObject(result, TextCheckReturn.class);
            Integer conclusionType = tcr.getConclusionType();
            if (conclusionType != 1 && !conclusionType.equals("1")) {
                map.put("code", ReturnStatusEnum.FAILURE.getValue());
                map.put("conclusion", tcr.getConclusion());
                map.put("log_id", tcr.getLog_id());
                map.put("data", tcr.getData());
                map.put("conclusionType", conclusionType);
                return map;
            }
            map.put("code", ReturnStatusEnum.SUCCESS.getValue());
            map.put("log_id", tcr.getLog_id());
            map.put("conclusion", tcr.getConclusion());
            map.put("conclusionType", tcr.getConclusionType());
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
