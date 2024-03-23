package com.TechieTroveHub.dao;

import com.TechieTroveHub.pojo.FollowingGroup;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * ClassName: FollowingGroupDao
 * Description:
 *
 * @Author agility6
 * @Create 2024/3/16 15:54
 * @Version: 1.0
 */
@Mapper
public interface FollowingGroupDao {
    FollowingGroup getByType(String type);

    FollowingGroup getById(Long id);

    List<FollowingGroup> getByUserId(Long userId);

    Integer addFollowingGroup(FollowingGroup followingGroup);

    List<FollowingGroup> getUserFollowingGroups(Long userId);
}
