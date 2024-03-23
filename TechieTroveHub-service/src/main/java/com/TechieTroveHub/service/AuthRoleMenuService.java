package com.TechieTroveHub.service;

import com.TechieTroveHub.POJO.auth.AuthRoleMenu;
import com.TechieTroveHub.dao.AuthRoleMenuDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * ClassName: AuthRoleMenuService
 * Description:
 *
 * @Author agility6
 * @Create 2024/3/22 22:22
 * @Version: 1.0
 */
@Service
public class AuthRoleMenuService {

    @Autowired
    private AuthRoleMenuDao authRoleMenuDao;

    public List<AuthRoleMenu> getAuthRoleMenusByRoleIds(Set<Long> roleIdSet) {
        return authRoleMenuDao.getAuthRoleMenusByRoleIds(roleIdSet);
    }
}
