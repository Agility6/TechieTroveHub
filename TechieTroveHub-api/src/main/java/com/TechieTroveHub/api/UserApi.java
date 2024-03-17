package com.TechieTroveHub.api;

import com.TechieTroveHub.POJO.JsonResponse;
import com.TechieTroveHub.POJO.PageResult;
import com.TechieTroveHub.POJO.User;
import com.TechieTroveHub.POJO.UserInfo;
import com.TechieTroveHub.service.UserFollowingService;
import com.TechieTroveHub.service.UserService;
import com.TechieTroveHub.support.UserSupport;
import com.TechieTroveHub.utils.RSAUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserFollowingService userFollowingService;

    @GetMapping("/users")
    public JsonResponse<User> getUserInfo() {
        Long userId = userSupport.getCurrentUserId();
        User user = userService.getUserInfo(userId);
        return new JsonResponse<>(user);
    }


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
    public JsonResponse<String> login(@RequestBody User user) throws Exception {
        String token = userService.login(user);
        return new JsonResponse<>(token);
    }

    @PutMapping("/users")
    public JsonResponse<String> updateUsers(@RequestBody User user) throws Exception {
        Long userId = userSupport.getCurrentUserId();
        user.setId(userId);
        userService.updateUsers(user);
        return JsonResponse.success();
    }

    @PutMapping("/user-infos")
    public JsonResponse<String> updateUserInfos(@RequestBody UserInfo userInfo) {
        Long userId = userSupport.getCurrentUserId();
        userInfo.setUserId(userId);
        userService.updateUserInfos(userInfo);
        return JsonResponse.success();
    }

    // 分页查询用户列表
    @GetMapping("/user-infos")
    public JsonResponse<PageResult<UserInfo>> pageListUserInfos(@RequestParam Integer no, @RequestParam Integer size, String nick) {
        Long userId = userSupport.getCurrentUserId();
        JSONObject params = new JSONObject();
        // TODO 待优化
        params.put("no", no);
        params.put("size", size);
        params.put("nick", nick);
        params.put("userId", userId);
        PageResult<UserInfo> result = userService.pageListUserInfos(params);
        // 没有被当前登录用户关注，才能进行关注。
        if (result.getTotal() > 0) {
            // 返回的还是UserInfo，但是会把UserInfo的followed属性进行标记
            // TODO 应该增加DTO或者VO
            List<UserInfo> checkedUserInfoList = userFollowingService.checkFollowingStatus(result.getList(), userId);
            result.setList(checkedUserInfoList);
        }

        return new JsonResponse<>(result);
    }
}
