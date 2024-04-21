package com.techietrovehub.service;

import com.techietrovehub.pojo.vo.UserInfoVo;

/**
 * ClassName: UserService
 * Description:
 *
 * @Author agility6
 * @Create 2024/4/16 19:55
 * @Version: 1.0
 */
public interface UserService {


    UserInfoVo getUserInfo(Long userId);
}
