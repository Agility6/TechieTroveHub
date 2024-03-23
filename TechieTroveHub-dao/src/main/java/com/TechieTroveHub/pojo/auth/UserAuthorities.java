package com.TechieTroveHub.pojo.auth;

import java.util.List;

/**
 * ClassName: UserAuthorities
 * Description:
 *
 * @Author agility6
 * @Create 2024/3/22 21:29
 * @Version: 1.0
 */
public class UserAuthorities {

    // 操作级的权限
    List<AuthRoleElementOperation> roleElementOperationList;

    // 页面菜单操作权限列表
    List<AuthRoleMenu> roleMenuList;

    public List<AuthRoleElementOperation> getRoleElementOperationList() {
        return roleElementOperationList;
    }

    public void setRoleElementOperationList(List<AuthRoleElementOperation> roleElementOperationList) {
        this.roleElementOperationList = roleElementOperationList;
    }

    public List<AuthRoleMenu> getRoleMenuList() {
        return roleMenuList;
    }

    public void setRoleMenuList(List<AuthRoleMenu> roleMenuList) {
        this.roleMenuList = roleMenuList;
    }
}
