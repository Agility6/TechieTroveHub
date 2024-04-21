package com.techietrovehub.mapper;

import com.techietrovehub.pojo.po.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * ClassName: UserMapper
 * Description:
 *
 * @Author agility6
 * @Create 2024/4/16 20:03
 * @Version: 1.0
 */
@Mapper
public interface UserMapper {
    userInfoVo getUserInfoByUserId(Long userId);
}
