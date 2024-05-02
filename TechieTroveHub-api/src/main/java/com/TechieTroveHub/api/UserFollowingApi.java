package com.TechieTroveHub.api;

import com.TechieTroveHub.pojo.FollowingGroup;
import com.TechieTroveHub.pojo.JsonResponse;
import com.TechieTroveHub.pojo.UserFollowing;
import com.TechieTroveHub.service.UserFollowingService;
import com.TechieTroveHub.support.UserSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClassName: UserFollowingApi
 * Description:
 *
 * @Author agility6
 * @Create 2024/3/16 16:07
 * @Version: 1.0
 */
@RestController
public class UserFollowingApi {

    @Autowired
    private UserFollowingService userFollowingService;

    @Autowired
    private UserSupport userSupport;

    /**
     * 增加关注
     * @param userFollowing
     * @return
     */
    @PostMapping("/user-followings")
    public JsonResponse<String> addUserFollowings(@RequestBody UserFollowing userFollowing) {
        Long userId = userSupport.getCurrentUserId();
        userFollowing.setUserId(userId);
        userFollowingService.addUserFollowings(userFollowing);
        return JsonResponse.success();
    }

    /**
     * 获取关注列表
     * @return
     */
    @GetMapping("/user-followings")
    public JsonResponse<List<FollowingGroup>> getUserFollowings() {
        Long userId = userSupport.getCurrentUserId();
        List<FollowingGroup> result = userFollowingService.getUserFollowings(userId);
        return new JsonResponse<>(result);
    }

    @DeleteMapping("/user-followings")
    public JsonResponse<String> deleteUserFollowing(@RequestParam Long followingId) {
        Long userId = userSupport.getCurrentUserId();
        userFollowingService.deleteUserFollowing(userId, followingId);
        return JsonResponse.success();
    }

    @PutMapping("/user-followings")
    public JsonResponse<String> updateUserFollowings(@RequestBody UserFollowing userFollowing) {
        Long userId = userSupport.getCurrentUserId();
        userFollowing.setUserId(userId);
        userFollowingService.updateUserFollowings(userFollowing);
        return JsonResponse.success();
    }

    /**
     * 获取粉丝列表
     * @return
     */
    @GetMapping("/user-fans")
    public JsonResponse<List<UserFollowing>> getUserFans() {
        Long userId = userSupport.getCurrentUserId();
        List<UserFollowing> result = userFollowingService.getUserFans(userId);
        return new JsonResponse<>(result);
    }

    /**
     * 增加关注分组
     * @param followingGroup
     * @return
     */
    @PostMapping("/user-following-groups")
    public JsonResponse<Long> addUserFollowingGroups(@RequestBody FollowingGroup followingGroup) {
        Long userId = userSupport.getCurrentUserId();
        followingGroup.setUserId(userId);
        Long groupId = userFollowingService.addUserFollowingGroups(followingGroup);
        return new JsonResponse<>(groupId);
    }

    /**
     * 获取关注分组
     * @return
     */
    @GetMapping("/user-following-groups")
    public JsonResponse<List<FollowingGroup>> getUserFollowingGroups() {
        Long userId = userSupport.getCurrentUserId();
        List<FollowingGroup> result = userFollowingService.getUserFollowingGroups(userId);
        return new JsonResponse<>(result);
    }
}
