package com.travelfourm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @author 34612
 * Redis配置类
 */
@Configuration
public class RedisConfig {

    /**
     * RedisTemplate是spring-data-redis中的方法，对redis的底层开发进行了高度封装
     * RedisTemplate对redis提供了各种操作，此处是对Redis自定义存储方式来观察key和value*/
    @Bean
    public RedisTemplate<String , Object> redisTemplate(RedisConnectionFactory factory){
        RedisTemplate<String , Object> template = new RedisTemplate<>();

        template.setConnectionFactory(factory);

        /**
         * 将Redis序列化是因为为了能更好的观察其存储的数据，
         * 如果不初始化，写入key-value值时会以二进制的方式写入Redis中*/
        //设置key的序列化方式
        template.setKeySerializer(RedisSerializer.string());

        //设置value的序列化方式
        template.setValueSerializer(RedisSerializer.json());

        //设置hash的key的序列化方式
        template.setHashKeySerializer(RedisSerializer.string());

        //设置hash的value的序列化方式
        template.setHashValueSerializer(RedisSerializer.json());

        template.afterPropertiesSet();
        return template;
    }
}
