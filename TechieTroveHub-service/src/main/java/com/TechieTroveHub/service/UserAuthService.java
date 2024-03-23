package com.TechieTroveHub.service;

import com.TechieTroveHub.POJO.auth.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.TechieTroveHub.POJO.constant.AuthRoleConstant.ROLE_LV0;

/**
 * ClassName: UserAuthService
 * Description:
 *
 * @Author agility6
 * @Create 2024/3/22 21:42
 * @Version: 1.0
 */
@Service
public class UserAuthService {

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private AuthRoleService authRoleService;

    public UserAuthorities getUserAuthorities(Long userId) {
        // 获取用户关联的角色，可能是一对多
        List<UserRole> userRoleList = userRoleService.getUserRoleByUserId(userId);
        // 获取角色Id
        Set<Long> roleIdSet = userRoleList.stream().map(UserRole::getRoleId).collect(Collectors.toSet());

        // 按钮操作权限
        List<AuthRoleElementOperation> roleElementOperationList = authRoleService.getRoleElementOperationsByRoleIds(roleIdSet);

        // 菜单操作权限
        List<AuthRoleMenu> authRoleMenuList = authRoleService.getAuthRoleMenusByRoleIds(roleIdSet);

        // 返回实体类
        UserAuthorities userAuthorities = new UserAuthorities();
        userAuthorities.setRoleElementOperationList(roleElementOperationList);
        userAuthorities.setRoleMenuList(authRoleMenuList);

        return userAuthorities;
    }

    public void addUserDefaultRole(Long id) {
        UserRole userRole = new UserRole();
        // 获取Lv0对应的roleId
        AuthRole role = authRoleService.getRoleByCode(ROLE_LV0);

        userRole.setUserId(id);
        userRole.setRoleId(role.getId());
        userRoleService.addUserRole (userRole);
    }
}
