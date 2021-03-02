package com.travelfourm;

import com.travelfourm.Util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = TravelfourmApplication.class)
public class SensitiveTest {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter(){
        String text="这里可以吃喝嫖赌，可以开票，可以吸毒，哈哈哈哈哈！";
        text = sensitiveFilter.filter(text);
        System.out.println(text);
    }
}
