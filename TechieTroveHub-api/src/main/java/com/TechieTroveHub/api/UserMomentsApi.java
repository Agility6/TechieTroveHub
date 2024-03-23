package com.TechieTroveHub.api;

import com.TechieTroveHub.pojo.JsonResponse;
import com.TechieTroveHub.pojo.UserMoment;
import com.TechieTroveHub.pojo.annotation.ApiLimitedRole;
import com.TechieTroveHub.pojo.annotation.DataLimited;
import com.TechieTroveHub.service.UserMomentsService;
import com.TechieTroveHub.support.UserSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.TechieTroveHub.pojo.constant.AuthRoleConstant.ROLE_LV0;


/**
 * ClassName: UserMomentsApi
 * Description:
 *
 * @Author agility6
 * @Create 2024/3/21 21:37
 * @Version: 1.0
 */
@RestController
public class UserMomentsApi {

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserMomentsService userMomentsService;

    /**
     * 增加动态
     * @param userMoment
     * @return
     * @throws Exception
     */
    @ApiLimitedRole(limitedRoleCodeList = {ROLE_LV0}) // 传入的Lv0是不允许调用增加动态的
    @DataLimited
    @PostMapping("/user-moments")
    public JsonResponse<String> addUserMoments(@RequestBody UserMoment userMoment) throws Exception {
        Long userId = userSupport.getCurrentUserId();
        userMoment.setUserId(userId);
        userMomentsService.addUserMoments(userMoment);
        return JsonResponse.success();
    }

    // 查询关注人的动态信息
    @GetMapping("/user-subscribed-moments")
    public JsonResponse<List<UserMoment>> getUserSubscribedMoments() {
        Long userId = userSupport.getCurrentUserId();
        List<UserMoment> list = userMomentsService.getUserSubscribedMoments(userId);
        return new JsonResponse<>(list);
    }

}
