package com.TechieTroveHub.handler;

import com.TechieTroveHub.POJO.JsonResponse;
import com.TechieTroveHub.POJO.exception.ConditionException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * ClassName: CommonGlobalExceptionHandler
 * Description:
 *
 * @Author agility6
 * @Create 2024/3/14 20:30
 * @Version: 1.0
 */
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE) // 优先级最高
public class CommonGlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public JsonResponse<String> commonExceptionHandler(HttpServletRequest request, Exception e) {

        String errorMsg = e.getMessage();
        if (e instanceof ConditionException) {
            String errorCode = ((ConditionException) e).getCode();
            return new JsonResponse<>(errorCode, errorMsg);
        } else {
            return new JsonResponse<>("500", errorMsg);
        }
    }

}
