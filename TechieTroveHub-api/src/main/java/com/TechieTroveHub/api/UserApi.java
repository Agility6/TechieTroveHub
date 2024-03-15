package com.TechieTroveHub.api;

import com.TechieTroveHub.POJO.JsonResponse;
import com.TechieTroveHub.POJO.User;
import com.TechieTroveHub.service.UserService;
import com.TechieTroveHub.utils.RSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: UserApi
 * Description:
 *
 * @Author agility6
 * @Create 2024/3/15 10:52
 * @Version: 1.0
 */
@RestController
public class UserApi {

    @Autowired // TODO有一些问题
    private UserService userService;

    @GetMapping("/rsa-pks")
    public JsonResponse<String> getRsaPublicKey() {
        String pk = RSAUtil.getPublicKeyStr();
        return new JsonResponse<>(pk);
    }

    @PostMapping("/users")
    public JsonResponse<String> addUser(@RequestBody User user) {
        userService.addUser(user);
        return JsonResponse.success();
    }

    @PostMapping("/user-tokens")
    public JsonResponse<String> login(@RequestBody User user) {
        String token = userService.login(user);
        return new JsonResponse<>(token);
    }
}
