package com.TechieTroveHub.api;

import com.TechieTroveHub.pojo.JsonResponse;
import com.TechieTroveHub.pojo.Video;
import com.TechieTroveHub.service.ElasticSearchService;
import com.TechieTroveHub.utils.FastDFSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.json.Json;

/**
 * ClassName: DemoApi
 * Description:
 *
 * @Author agility6
 * @Create 2024/3/26 19:00
 * @Version: 1.0
 */
@RestController
public class DemoApi {

    @Autowired
    private FastDFSUtil fastDFSUtil;

    @Autowired
    private ElasticSearchService elasticSearchService;

    @GetMapping("/slices")
    public void slices(MultipartFile file) throws Exception {
        fastDFSUtil.convertFileToSlices(file);
    }

    /**
     * 从es中获取video
     * @param keyword
     * @return
     */
    public JsonResponse<Video> getEsVideos(@RequestParam String keyword) {
        Video video = elasticSearchService.getVideos(keyword);
        return new JsonResponse<>(video);
    }
}
