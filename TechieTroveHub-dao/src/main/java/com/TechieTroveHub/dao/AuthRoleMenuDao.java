package com.TechieTroveHub.dao;

import com.TechieTroveHub.pojo.auth.AuthRoleMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Set;

/**
 * ClassName: AuthRoleMenuDao
 * Description:
 *
 * @Author agility6
 * @Create 2024/3/22 22:40
 * @Version: 1.0
 */
@Mapper
public interface AuthRoleMenuDao {
    List<AuthRoleMenu> getAuthRoleMenusByRoleIds(Set<Long> roleIdSet);
}
