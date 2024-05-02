package com.TechieTroveHub.api;

import com.TechieTroveHub.pojo.JsonResponse;
import com.TechieTroveHub.pojo.Tag;
import com.TechieTroveHub.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: TagApi
 * Description:
 *
 * @Author agility6
 * @Create 2024/5/1 15:38
 * @Version: 1.0
 */
@RestController
public class TagApi {

    @Autowired
    private TagService tagService;

    @PostMapping("/tags")
    public JsonResponse<Long> addTag(@RequestBody Tag tag) {
        Long tagId = tagService.addTag(tag);
        return new JsonResponse<>(tagId);
    }
}
