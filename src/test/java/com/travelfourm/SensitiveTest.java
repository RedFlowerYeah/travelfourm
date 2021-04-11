package com.travelfourm;

//import com.travelfourm.Util.CheckContent;
import com.travelfourm.Util.CheckContent;
import com.travelfourm.entity.DiscussPost;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
        //Map<String, Object> map1 = CheckContent.checkText("你好");
        //失败
        Map<String, Object> map = CheckContent.checkText("习近平狗东西");

        Iterator<String> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Object value = map.get(key);
            System.out.println(key + ":" + value);
        }
    }

}
