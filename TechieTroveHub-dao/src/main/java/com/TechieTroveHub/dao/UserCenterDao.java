package com.TechieTroveHub.dao;

import com.TechieTroveHub.pojo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ClassName: UserCenterDao
 * Description:
 *
 * @Author agility6
 * @Create 2024/5/1 16:23
 * @Version: 1.0
 */
@Mapper
public interface UserCenterDao {
    List<VideoArea> getUserCenterVideoAreas(Long userId);

    Integer pageCountUserCenterVideos(Map<String, Object> params);

    List<Video> pageListUserCenterVideos(Map<String, Object> params);

    List<CollectionGroup> countUserCenterCollectionGroups(Long userId);

    Integer pageCountUserCollections(Map<String, Object> params);

    List<VideoCollection> pageListUserCollections(Map<String, Object> params);

    List<Video> getVideoInfoByIds(Set<Long> videoIds);

    int addUserCollectionGroups(VideoCollectionGroup videoCollectionGroup);

    List<FollowingGroup> getUserFollowingGroups(Long userId);

    List<FollowingGroup> countUserCenterFollowingGroups(Long userId);

    Integer pageCountUserCenterFollowings(Map<String, Object> params);

    List<UserFollowing> pageListUserCenterFollowings(Map<String, Object> params);

    // TODO
    List<UserInfo> getUserInfoByIds(@Param("userIds") Set<Long> userIds);

    Integer pageCountUserFans(Map<String, Object> params);

    List<UserFollowing> pageListUserFans(Map<String, Object> params);

    List<UserFollowing> getUserFollowings(Long userId);

    Long countUserFans(Long userId);
}
