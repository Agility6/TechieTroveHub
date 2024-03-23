package com.TechieTroveHub.dao;

import com.TechieTroveHub.POJO.auth.AuthRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * ClassName: AuthRoleDao
 * Description:
 *
 * @Author agility6
 * @Create 2024/3/23 15:02
 * @Version: 1.0
 */
@Mapper
public interface AuthRoleDao {
    AuthRole getRoleByCode(String code);
}
