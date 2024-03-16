package com.TechieTroveHub.support;

import com.TechieTroveHub.POJO.exception.ConditionException;
import com.TechieTroveHub.utils.TokenUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * ClassName: UserSupport
 * Description:
 *
 * @Author agility6
 * @Create 2024/3/15 20:59
 * @Version: 1.0
 */
@Component
public class UserSupport {

    public Long getCurrentUserId() {
        // 获取请求的信息
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert requestAttributes != null;
        String token = requestAttributes.getRequest().getHeader("token");
        Long userId = TokenUtil.verifyToken(token);
        if (userId < 0) {
            throw new ConditionException("非法用户！");
        }
        return userId;
    }
}
