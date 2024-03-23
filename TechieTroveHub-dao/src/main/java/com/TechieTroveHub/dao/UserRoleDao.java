package com.TechieTroveHub.dao;

import com.TechieTroveHub.POJO.auth.UserRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * ClassName: UserRoleDao
 * Description:
 *
 * @Author agility6
 * @Create 2024/3/22 22:10
 * @Version: 1.0
 */
@Mapper
public interface UserRoleDao {
    List<UserRole> getUserRoleByUserId(Long userId);

    Integer addUserRole(UserRole userRole);
}
