package com.TechieTroveHub.service;

import com.TechieTroveHub.POJO.auth.AuthRole;
import com.TechieTroveHub.POJO.auth.AuthRoleElementOperation;
import com.TechieTroveHub.POJO.auth.AuthRoleMenu;
import com.TechieTroveHub.dao.AuthRoleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * ClassName: AuthRoleService
 * Description:
 *
 * @Author agility6
 * @Create 2024/3/22 21:50
 * @Version: 1.0
 */
@Service
public class AuthRoleService {

    // 查询操作权限
    @Autowired
    private AuthRoleElementOperationService authRoleElementOperationService;

    // 查询菜单关联权限
    @Autowired
    private AuthRoleMenuService authRoleMenuService;

    @Autowired
    private AuthRoleDao authRoleDao;

    public List<AuthRoleElementOperation> getRoleElementOperationsByRoleIds(Set<Long> roleIdSet) {
        return authRoleElementOperationService.getRoleElementOperationsByRoleIds(roleIdSet);
    }

    public List<AuthRoleMenu> getAuthRoleMenusByRoleIds(Set<Long> roleIdSet) {
        return authRoleMenuService.getAuthRoleMenusByRoleIds(roleIdSet);
    }

    public AuthRole getRoleByCode(String code) {

        return authRoleDao.getRoleByCode(code);
    }
}
