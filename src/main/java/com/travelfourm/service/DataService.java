package com.travelfourm.service;

import com.travelfourm.Util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class DataService {
    @Autowired
    private RedisTemplate redisTemplate;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

    //将指定ip计入UV
    public void recordUV(String ip){
        String redisKey = RedisKeyUtil.getUVkey(simpleDateFormat.format(new Date()));
        redisTemplate.opsForHyperLogLog().add(redisKey,ip);
    }

    //统计日期范围内的UV
    /**
     * 此处存在bug，若是前一个日期>后一个日期，执行会报错*/
    public long calculateUV(Date start,Date end){

        if (start == null || end ==null) {
            throw new IllegalArgumentException("参数不能为空！");
        }

        //整理日期范围内的key
        List<String> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);


        while (!calendar.getTime().after(end)){
            String key = RedisKeyUtil.getUVkey(simpleDateFormat.format(calendar.getTime()));
            keyList.add(key);
            calendar.add(Calendar.DATE,1);
        }


        //合并统计数据
        String redisKey = RedisKeyUtil.getUVKey(simpleDateFormat.format(start),simpleDateFormat.format(end));
        redisTemplate.opsForHyperLogLog().union(redisKey,keyList.toArray());

        //返回统计的结果
        return redisTemplate.opsForHyperLogLog().size(redisKey);
    }

    //将指定用户计入DAU
    public void recordDAU(int userId){
        String redisKey = RedisKeyUtil.getDAUKey(simpleDateFormat.format(new Date()));
        redisTemplate.opsForValue().setBit(redisKey,userId,true);
    }

    //统计指定日期范围内的DAU
    public long calculateDAU(Date start,Date end){
        if (start == null || end == null){
            throw new IllegalArgumentException("参数不能为空!");
        }

        //整理该日期范围内的DAU
        List<byte[]> keyList = new ArrayList<>();
        Calendar calendar  = Calendar.getInstance();

        while (!calendar.getTime().after(end)){
            String key = RedisKeyUtil.getUVkey(simpleDateFormat.format(calendar.getTime()));
            keyList.add(key.getBytes());
            calendar.add(Calendar.DATE,1);
        }

        //进行OR运算
        return (long) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                String redisKey = RedisKeyUtil.getDAUKey(simpleDateFormat.format(start),simpleDateFormat.format(end));
                redisConnection.bitOp(RedisStringCommands.BitOperation.OR,
                        redisKey.getBytes(),keyList.toArray(new byte[0][0]));
                return redisConnection.bitCount(redisKey.getBytes());
            }
        });
    }
}
