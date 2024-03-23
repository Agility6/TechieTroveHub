package com.TechieTroveHub.service;

import com.TechieTroveHub.POJO.auth.UserRole;
import com.TechieTroveHub.dao.UserRoleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * ClassName: UserRoleService
 * Description:
 *
 * @Author agility6
 * @Create 2024/3/22 21:50
 * @Version: 1.0
 */
@Service
public class UserRoleService {

    @Autowired
    private UserRoleDao userRoleDao;

    public List<UserRole> getUserRoleByUserId(Long userId) {
        return userRoleDao.getUserRoleByUserId(userId);
    }

    public void addUserRole(UserRole userRole) {
        userRole.setCreateTime(new Date());
        userRoleDao.addUserRole(userRole);
    }
}
