package com.techietrovehub.service;

/**
 * ClassName: RedisService
 * Description:
 *
 * @Author agility6
 * @Create 2024/4/16 20:13
 * @Version: 1.0
 */
public interface RedisService {

    /**
     * 获取数据
     * @param key
     * @return
     */
    String get(String key);
}
