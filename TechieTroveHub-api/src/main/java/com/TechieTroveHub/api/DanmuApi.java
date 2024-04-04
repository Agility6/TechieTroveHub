package com.TechieTroveHub.api;

import com.TechieTroveHub.pojo.Danmu;
import com.TechieTroveHub.pojo.JsonResponse;
import com.TechieTroveHub.service.DanmuService;
import com.TechieTroveHub.support.UserSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ClassName: DanmuApi
 * Description:
 *
 * @Author agility6
 * @Create 2024/4/4 17:20
 * @Version: 1.0
 */
@RestController
public class DanmuApi {

    @Autowired
    private DanmuService danmuService;

    @Autowired
    private UserSupport userSupport;

    /**
     * 查询弹幕
     * @param videoId
     * @param startTime
     * @param endTime
     * @return
     * @throws Exception
     */
    @GetMapping("/danmus")
    public JsonResponse<List<Danmu>> getDanmus(@RequestParam Long videoId,
                                               String startTime,
                                               String endTime) throws Exception {
        List<Danmu> list;

        try {
            // 判断当前是游客模式还是用户登录模式
            userSupport.getCurrentUserId();
            // 若是用户登录模式，则允许用户进行时间段筛选
            list = danmuService.getDanmus(videoId, startTime, endTime);
        } catch (Exception ignored) {
            //  若为游客模式，则不允许用户进行时间段筛选
            list = danmuService.getDanmus(videoId, null, null);
        }

        return new JsonResponse<>(list);
    }
}
