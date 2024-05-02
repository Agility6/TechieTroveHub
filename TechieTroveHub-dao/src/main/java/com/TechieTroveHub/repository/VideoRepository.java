package com.TechieTroveHub.repository;

import com.TechieTroveHub.pojo.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * ClassName: VideoRepository
 * Description:
 *
 * @Author agility6
 * @Create 2024/4/4 18:17
 * @Version: 1.0
 */
public interface VideoRepository extends ElasticsearchRepository<Video, Long> {

    // find by title like
    Video findByTitleLike(String keyword);

    long countByTitleOrDescription(String titleKeyword, String DescriptionKeyword);

    Page<Video> findByTitleOrDescriptionOrderByViewCountDesc(String title, String description, PageRequest pageRequest);

    Page<Video> findByTitleOrDescriptionOrderByCreateTimeDesc(String title, String description, PageRequest pageRequest);

    Page<Video> findByTitleOrDescriptionOrderByDanmuCountDesc(String title, String description, PageRequest pageRequest);
}
