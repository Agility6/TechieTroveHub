package com.TechieTroveHub.api;

import com.TechieTroveHub.pojo.Content;
import com.TechieTroveHub.pojo.JsonResponse;
import com.TechieTroveHub.service.ContentService;
import com.mysql.cj.log.Log;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: ContentApi
 * Description:
 *
 * @Author agility6
 * @Create 2024/4/30 14:37
 * @Version: 1.0
 */
@RestController
public class ContentApi {

    private ContentService contentService;

    public JsonResponse<Long> addContent(@RequestBody Content content) {
        Long contentId = contentService.addContent(content);
        return new JsonResponse<>(contentId);
    }

}
