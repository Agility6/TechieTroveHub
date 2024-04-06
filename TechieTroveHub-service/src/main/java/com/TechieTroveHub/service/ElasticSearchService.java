package com.TechieTroveHub.service;

import com.TechieTroveHub.pojo.UserInfo;
import com.TechieTroveHub.pojo.Video;
import com.TechieTroveHub.repository.UserInfoRepository;
import com.TechieTroveHub.repository.VideoRepository;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * ClassName: ElasticSearchService
 * Description:
 *
 * @Author agility6
 * @Create 2024/4/4 18:15
 * @Version: 1.0
 */
@Service
public class ElasticSearchService {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 将数据添加到es中
     * @param video
     */
    public void addVideo(Video video) {
        videoRepository.save(video);
    }

    public void addUserInfo(UserInfo userInfo) {
        userInfoRepository.save(userInfo);
    }

    /**
     * 模糊查询
     * @param keyword
     */
    public Video getVideos(String keyword) {
        return videoRepository.findByTitleLike(keyword);
    }

    public void deleteAllVideos() {
        videoRepository.deleteAll();
    }

    /**
     *
     * @param keyword 关键字
     * @param pageNo 页码
     * @param pageSize 页码大小
     * @return
     */
    public List<Map<String, Object>> getContents(String keyword,
                                                 Integer pageNo,
                                                 Integer pageSize) throws IOException {

        // 定义搜索的索引
        String[] indices = {"videos", "user-infos"};

        // 创建搜索请求对象
        SearchRequest searchRequest = new SearchRequest(indices);
        // 建一个搜索源构建器，用于构建搜索请求的查询。
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // 分页
        sourceBuilder.from(pageNo - 1);
        sourceBuilder.size(pageSize);

        // 创建一个多字段匹配查询构建器
        MultiMatchQueryBuilder matchQueryBuilder = QueryBuilders.multiMatchQuery(keyword, "title", "nick", "description");

        // 查询构建器添加到搜索源构建器中
        sourceBuilder.query(matchQueryBuilder);
        searchRequest.source(sourceBuilder);
        // 设置超时时间
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        // 高亮显示
        String[] array = {"title", "nick", "description"};
        HighlightBuilder highlightBuilder = new HighlightBuilder();

        for (String str : array) {
           // 为每个字段添加了高亮配置
            highlightBuilder.fields().add(new HighlightBuilder.Field(str));
        }

        // 如果要多个字段进行高亮，设置为false
        highlightBuilder.requireFieldMatch(false);

        // 设置高亮样式
        highlightBuilder.preTags("<span style=\"color:red\">");
        highlightBuilder.postTags("</span>");
        // 将高亮构建器添加到搜索源构建器中。
        sourceBuilder.highlighter(highlightBuilder);

        // 执行搜索请求，获取搜索响应
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        List<Map<String, Object>> arrayList = new ArrayList<>();

        for (SearchHit hit : searchResponse.getHits()) {
            // 键是字段名，值是高亮字段对象
            Map<String, HighlightField> highLightBuilderFields = hit.getHighlightFields();
            // 从当前命中中获取原始的字段数, getSourceAsMap方法返回了一个包含命中文档的字段映射的Map
            Map<String, Object> sourceMap = hit.getSourceAsMap();

            for (String key : array) { // 遍历预定义的要高亮的字段数组
                // 从高亮字段Map中获取指定字段名的高亮字段对象。
                HighlightField field = highLightBuilderFields.get(key);
                if (field != null) { // 检查是否存在高亮字段。
                    // 从高亮字段对象中获取高亮片段
                    Text[] fragments = field.fragments();
                    String str = Arrays.toString(fragments);
                    // TODO ?
                    System.out.println("str ===> " + str);
                    str = str.substring(1, str.length() - 1);
                    // 处理后的高亮文本替换原始字段的内容
                    sourceMap.put(key, str);
                }
            }
            arrayList.add(sourceMap);
        }
        return arrayList;
    }
}
