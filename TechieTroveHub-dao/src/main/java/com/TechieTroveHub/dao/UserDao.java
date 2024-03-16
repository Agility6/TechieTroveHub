package com.TechieTroveHub.dao;

import com.TechieTroveHub.POJO.User;
import com.TechieTroveHub.POJO.UserInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * ClassName: UserDao
 * Description:
 *
 * @Author agility6
 * @Create 2024/3/15 10:53
 * @Version: 1.0
 */
@Mapper
public interface UserDao {

    User getUserByPhone(String phone);

    Integer addUser(User user);

    Integer addUserInfo(UserInfo userInfo);

    User getUserById(Long id);

    UserInfo getUserInfoByUserId(Long userId);
}
