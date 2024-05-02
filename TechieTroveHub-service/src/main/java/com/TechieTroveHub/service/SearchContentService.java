package com.TechieTroveHub.service;

import com.TechieTroveHub.pojo.UserInfo;
import com.TechieTroveHub.pojo.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import javax.xml.ws.Action;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassName: SearchContentService
 * Description:
 *
 * @Author agility6
 * @Create 2024/5/1 15:13
 * @Version: 1.0
 */
@Service
public class SearchContentService {

    @Autowired
    private ElasticSearchService elasticSearchService;

    @Value("${fdfs.http.storage-addr}")
    private String fastdfsUrl;

    public Map<String, Object> countBySearchTxt(String searchTxt) {
        Map<String, Object> result = new HashMap<>();

        // 计算视频
        long videoCount = elasticSearchService.countVideoBySearchTxt(searchTxt);

        // 计算用户
        long userCount = elasticSearchService.countUserBySearchTxt(searchTxt);

        result.put("videoCount", videoCount);
        result.put("userCount", userCount);

        return result;
    }

    public Page<Video> pageListSearchVideos(String keyword, Integer pageSize, Integer pageNo, String searchType) {

        Page<Video> result = elasticSearchService.pageListSearchVideos(keyword, pageSize, pageNo - 1, searchType);

        result.getContent().forEach(item -> item.setThumbnail(fastdfsUrl + item.getThumbnail()));
        return result;
    }

    public Page<UserInfo> pageListSearchUsers(String keyword, Integer pageSize, Integer pageNo, String searchType) {

        Page<UserInfo> result = elasticSearchService.pageListSearchUsers(keyword, pageSize, pageNo - 1, searchType);

        return result;
    }
}
