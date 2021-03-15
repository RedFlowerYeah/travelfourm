package com.travelfourm;

import com.travelfourm.service.CheckContentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Iterator;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = TravelfourmApplication.class)
public class SensitiveTest {

    @Test
    public void testText() {
        //成功
        Map<String, Object> map = CheckContentService.checkText("你好");
        //失败
        //Map<String, Object> map = CheckContentService.checkText("习近平狗东西");

        Iterator<String> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Object value = map.get(key);
            System.out.println(key + ":" + value);
        }
    }

}
