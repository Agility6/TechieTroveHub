package com.TechieTroveHub.aspect;

import com.TechieTroveHub.pojo.UserMoment;
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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.TechieTroveHub.pojo.constant.AuthRoleConstant.ROLE_LV0;

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
public class DataLimitedAspect {

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserRoleService userRoleService;

    @Pointcut("@annotation(com.TechieTroveHub.pojo.annotation.DataLimited)")
    public void check() {}

    @Before("check()")
    public void doBefore(JoinPoint joinPoint) {
        // 角色比对
        Long userId = userSupport.getCurrentUserId();
        List<UserRole> userRoleList = userRoleService.getUserRoleByUserId(userId);
        Set<String> roleCodeSet = userRoleList.stream().map(UserRole::getRoleCode).collect(Collectors.toSet());

        // 可以获取到切面方法的参数
        // TODO 该方法主要用于控制UserMoment的type类型字段
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
           if (arg instanceof UserMoment) { // 如果属于UserMoment类型
                UserMoment userMoment = (UserMoment) arg;
               String type = userMoment.getType();
               // 如果该用户的等级是Lv0且数据不为0
               if (roleCodeSet.contains(ROLE_LV0) && !"0".equals(type)) {
                    throw new ConditionException("参数异常");
               }
           }
        }
    }

}
