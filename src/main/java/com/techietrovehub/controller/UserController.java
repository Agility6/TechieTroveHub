package com.techietrovehub.controller;

import com.techietrovehub.pojo.JsonResponse;
import com.techietrovehub.pojo.po.User;
import com.techietrovehub.pojo.vo.UserInfoVo;
import com.techietrovehub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: UserController
 * Description:
 *
 * @Author agility6
 * @Create 2024/4/16 19:48
 * @Version: 1.0
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public JsonResponse<UserInfoVo> getUserInfo() {
        Long userId = 1l;
        UserInfoVo userInfoVo = userService.getUserInfo(userId);
        return new JsonResponse<>(userInfoVo);
    }

}
