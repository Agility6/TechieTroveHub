package com.TechieTroveHub.aspect;

import com.TechieTroveHub.pojo.annotation.ApiLimitedRole;
import com.TechieTroveHub.pojo.auth.UserRole;
import com.TechieTroveHub.pojo.exception.ConditionException;
import com.TechieTroveHub.service.UserRoleService;
import com.TechieTroveHub.support.UserSupport;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ClassName: ApiLimitedRoleAspect
 * Description:
 *
 * @Author agility6
 * @Create 2024/3/23 14:26
 * @Version: 1.0
 */
@Order(1) // 优先级
@Component
@Aspect
public class ApiLimitedRoleAspect {

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserRoleService userRoleService;

    @Pointcut("@annotation(com.TechieTroveHub.pojo.annotation.ApiLimitedRole)")
    public void check() {}

    @Before("check() && @annotation(apiLimitedRole)")
    public void doBefore(JoinPoint joinPoint, ApiLimitedRole apiLimitedRole) {
        // 角色比对
        Long userId = userSupport.getCurrentUserId();
        List<UserRole> userRoleList = userRoleService.getUserRoleByUserId(userId);
        String[] limitedRoleCodeList = apiLimitedRole.limitedRoleCodeList();

        Set<String> limitedRoleCodeSet = Arrays.stream(limitedRoleCodeList).collect(Collectors.toSet());
        Set<String> roleCodeSet = userRoleList.stream().map(UserRole::getRoleCode).collect(Collectors.toSet());

        // 取交集
        roleCodeSet.removeAll(limitedRoleCodeSet);

        if (!roleCodeSet.isEmpty()) {
            throw new ConditionException("权限不足！");
        }
    }

}
