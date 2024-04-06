package com.TechieTroveHub.repository;

import com.TechieTroveHub.pojo.UserInfo;
import com.TechieTroveHub.pojo.Video;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * ClassName: UserInfoRepository
 * Description:
 *
 * @Author agility6
 * @Create 2024/4/4 18:17
 * @Version: 1.0
 */
public interface UserInfoRepository extends ElasticsearchRepository<UserInfo, Long> {
}
