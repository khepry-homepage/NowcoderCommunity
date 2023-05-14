package com.nowcoder.community.service;


import com.nowcoder.community.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

@Service
public class StatisticsService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
    public void addUV(String ip) {
        if (ip == null) {
            throw new IllegalArgumentException("无效ip");
        }
        String key = RedisKeyUtil.getUVKey(df.format(new Date()));
        redisTemplate.opsForHyperLogLog().add(key, ip);
    }
    public void addDAU(int userId) {
        String key = RedisKeyUtil.getDAUKey(df.format(new Date()));
        redisTemplate.opsForValue().setBit(key, userId, true);
    }
    public long getUVCount(Date start, Date end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("无效日期");
        }
        String key = RedisKeyUtil.getUVIntervalKey(start, end);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        List<String> keys = new ArrayList<>();
        while (!calendar.getTime().after(end)) {
            String uvKey = RedisKeyUtil.getUVKey(df.format(calendar.getTime()));
            keys.add(uvKey);
            calendar.add(Calendar.DATE, 1);
        }
        redisTemplate.opsForHyperLogLog().union(key, keys.toArray(new String[0]));
        return redisTemplate.opsForHyperLogLog().size(key);
    }
    public long getDAUCount(Date start, Date end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("无效日期");
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        List<byte[]> keys = new ArrayList<>();
        while (!calendar.getTime().after(end)) {
            String dauKey = RedisKeyUtil.getDAUKey(df.format(calendar.getTime()));
            keys.add(dauKey.getBytes());
            calendar.add(Calendar.DATE, 1);
        }
        //  进行位图间的或运算
        return (long) redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                String key = RedisKeyUtil.getDAUIntervalKey(start, end);
                connection.bitOp(RedisStringCommands.BitOperation.OR, key.getBytes(), keys.toArray(new byte[0][0]));
                return connection.bitCount(key.getBytes());
            }
        });
    }
}
