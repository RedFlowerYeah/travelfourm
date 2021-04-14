package com.travelfourm.Util;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.Reader;
import java.io.StringReader;

/**
 * @Author 34612
 * @CreateTime 2021/4/1 19:42
 */
public class XmlBuilderUtil {
    /**
     * 将XML转换为指定的POJO*/
    public static Object xmlStrToObject(String xmlStr, Class<?> clazz) throws Exception {
        Object xmlObject = null;
        JAXBContext context = JAXBContext.newInstance(clazz);
        // XML转换为对象的接口
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Reader in = new StringReader(xmlStr);
        xmlObject = unmarshaller.unmarshal(in);

        if (null != in) {
            in.close();
        }
        return xmlObject;
    }
}
