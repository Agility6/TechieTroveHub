package com.TechieTroveHub.POJO.exception;

import org.springframework.core.codec.CodecException;

/**
 * ClassName: ConditionException
 * Description: 条件异常
 *
 * @Author agility6
 * @Create 2024/3/14 20:31
 * @Version: 1.0
 */
public class ConditionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String code;

    public ConditionException(String code, String name) {
        super(name);
        this.code = code;
    }

    public ConditionException(String name) {
        super(name);
        code = "500";
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
