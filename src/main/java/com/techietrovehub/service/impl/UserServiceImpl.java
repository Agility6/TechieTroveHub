package com.techietrovehub.service.impl;

import com.alibaba.fastjson.JSON;
import com.mysql.cj.util.StringUtils;
import com.techietrovehub.common.constants.RedisConstants;
import com.techietrovehub.mapper.UserMapper;
import com.techietrovehub.pojo.po.UserInfo;
import com.techietrovehub.pojo.vo.UserInfoVo;
import com.techietrovehub.service.RedisService;
import com.techietrovehub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * ClassName: UserServiceImpl
 * Description:
 *
 * @Author agility6
 * @Create 2024/4/16 19:55
 * @Version: 1.0
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public UserInfoVo getUserInfo(Long userId) {
        /**
         *  获取用户粉丝的数量
         *  获取用户关注人数量
         */

        UserInfoVo userInfoVo;

        // TODO 本地缓存热点数据

        // 1. 通过Redis获取用户信息
        String userJson = redisService.get(RedisConstants.USERINFO + userId);
        if (!StringUtils.isNullOrEmpty(userJson)) { // redis存在该用户
            UserInfo userInfo = JSON.parseObject(userJson, UserInfo.class);
            // 将userInfo拼装 ==> userInfoVo
            user
            // TODO 缺少该用户，关注、粉丝信息，单独的计数服务实现
            return userInfoVo;
        }

        // TODO 缺少该用户，关注、粉丝信息，单独的计数服务实现

        // 2. Redis中没有该用户，数据中查找
        userInfoVo = userMapper.getUserInfoByUserId(userId);

        // 3. 存放到Redis中
        redisService.set(RedisConstants.USERINFO + userId, JSON.toJSON(userInfoVo));

        return userInfoVo;
    }


    public updateUser {


        // password

        // 1. redis
        // 2. mysql
    }
}
