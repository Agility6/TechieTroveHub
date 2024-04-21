package com.techietrovehub.service.impl;

import com.techietrovehub.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * ClassName: RedisServiceImpl
 * Description:
 *
 * @Author agility6
 * @Create 2024/4/16 20:14
 * @Version: 1.0
 */
@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }
}
