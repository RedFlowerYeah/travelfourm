package com.travelfourm.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @author 34612
 * KatchaConfig（验证码的配置类）
 */

@Configuration
public class KaptchaConfig {

    /**
     * 对kaptchar进行配置
     * Properties properties=new Properties(); 读取配置文件
     * */
    @Bean
    public Producer kaptchaProducer(){
        Properties properties=new Properties();

        /**
         * 设置宽度*/
        properties.setProperty("kaptcha.image.width","100");

        /**
         * 设置高度*/
        properties.setProperty("kaptcha.image.height","40");

        /**
         * 字体大小*/
        properties.setProperty("kaptcha.textproducer.font.size","32");

        /**
         * 字体颜色*/
        properties.setProperty("kaptcha.textproducer.font.color","0,0,0");

        /**
         * 创建的字符集合*/
        properties.setProperty("kaptcha.textproducer.char.string","0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");

        /**
         * 验证码长度*/
        properties.setProperty("kaptcha.textproducer.char.length","4");

        /**
         * 噪音生产者，干扰的颜色*/
        properties.setProperty("kaptcha.noise.impl","com.google.code.kaptcha.impl.NoNoise");

        /**
         * 创建一个默认的kaptcha*/
        DefaultKaptcha kaptcha=new DefaultKaptcha();

        /**
         * 将设置的属性放入到config中*/
        Config config=new Config(properties);
        kaptcha.setConfig(config);

        /**
         * 返回设置成功的kaptcha*/
        return kaptcha;
    }
}
