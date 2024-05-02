package com.TechieTroveHub.api;

import com.TechieTroveHub.pojo.JsonResponse;
import com.TechieTroveHub.pojo.PageResult;
import com.TechieTroveHub.pojo.UserVideoHistory;
import com.TechieTroveHub.service.UserHistoryService;
import com.TechieTroveHub.support.UserSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: UserHistoryApi
 * Description:
 *
 * @Author agility6
 * @Create 2024/5/2 17:39
 * @Version: 1.0
 */
@RestController
public class UserHistoryApi {

    @Autowired
    private UserHistoryService userHistoryService;

    @Autowired
    private UserSupport userSupport;


    @GetMapping("/user-video-histories")
    public JsonResponse<PageResult<UserVideoHistory>> pagListUserVideoHistory(@RequestParam Integer size, @RequestParam Integer no) {
        Long userId = userSupport.getCurrentUserId();
        PageResult<UserVideoHistory> result = userHistoryService.pagListUserVideoHistory(size, no, userId);
        return new JsonResponse<>(result);
    }

}
