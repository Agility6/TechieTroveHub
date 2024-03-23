package com.TechieTroveHub.pojo.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * ClassName: ApiLimitedRole
 * Description:
 *
 * @Author agility6
 * @Create 2024/3/23 14:24
 * @Version: 1.0
 */
@Retention(RetentionPolicy.RUNTIME) // 运行阶段
@Target({ElementType.METHOD})
@Documented
@Component
public @interface ApiLimitedRole {

    String[] limitedRoleCodeList() default {};
}
