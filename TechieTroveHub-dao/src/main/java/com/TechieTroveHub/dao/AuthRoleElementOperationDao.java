package com.TechieTroveHub.dao;

import com.TechieTroveHub.POJO.auth.AuthRoleElementOperation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * ClassName: AuthRoleElementOperationDao
 * Description:
 *
 * @Author agility6
 * @Create 2024/3/22 22:23
 * @Version: 1.0
 */
@Mapper
public interface AuthRoleElementOperationDao {
    List<AuthRoleElementOperation> getRoleElementOperationsByRoleIds(@Param("roleIdSet") Set<Long> roleIdSet);
}
