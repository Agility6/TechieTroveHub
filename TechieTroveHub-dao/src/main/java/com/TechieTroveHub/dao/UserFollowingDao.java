package com.TechieTroveHub.dao;

import com.TechieTroveHub.pojo.UserFollowing;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * ClassName: UserFollowingDao
 * Description:
 *
 * @Author agility6
 * @Create 2024/3/16 15:54
 * @Version: 1.0
 */
@Mapper
public interface UserFollowingDao {
    Integer deleteUserFollowing(@Param("userId") Long userId, @Param("followingId") Long followingId);

    Integer addUserFollowing(UserFollowing userFollowing);

    List<UserFollowing> getUserFollowings(Long userId);

    List<UserFollowing> getUserFans(Long userId);
}
