package com.TechieTroveHub.api;

import com.TechieTroveHub.pojo.JsonResponse;
import com.TechieTroveHub.service.ElasticSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * ClassName: SystemApi
 * Description:
 *
 * @Author agility6
 * @Create 2024/4/5 10:34
 * @Version: 1.0
 */
@RestController
public class SystemApi {

    @Autowired
    private ElasticSearchService elasticSearchService;

    @GetMapping("/contents")
    public JsonResponse<List<Map<String, Object>>> getContents(@RequestParam String keyword,
                                                               @RequestParam Integer pageNo,
                                                               @RequestParam Integer pageSize) throws IOException {
        List<Map<String, Object>> list = elasticSearchService.getContents(keyword, pageNo, pageSize);
        return new JsonResponse<>(list);
    }
}
