package com.TechieTroveHub.service;

import com.TechieTroveHub.POJO.auth.AuthRoleElementOperation;
import com.TechieTroveHub.dao.AuthRoleElementOperationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * ClassName: AuthRoleElementOperationService
 * Description:
 *
 * @Author agility6
 * @Create 2024/3/22 22:20
 * @Version: 1.0
 *
 */
@Service
public class AuthRoleElementOperationService {

    @Autowired
    private AuthRoleElementOperationDao authRoleElementOperationDao;

    public List<AuthRoleElementOperation> getRoleElementOperationsByRoleIds(Set<Long> roleIdSet) {
        return authRoleElementOperationDao.getRoleElementOperationsByRoleIds(roleIdSet);
    }
}
