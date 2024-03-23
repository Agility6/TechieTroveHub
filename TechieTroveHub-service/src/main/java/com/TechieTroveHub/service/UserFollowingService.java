package com.TechieTroveHub.service;

import com.TechieTroveHub.pojo.FollowingGroup;
import com.TechieTroveHub.pojo.User;
import com.TechieTroveHub.pojo.UserFollowing;
import com.TechieTroveHub.pojo.UserInfo;
import com.TechieTroveHub.pojo.exception.ConditionException;
import com.TechieTroveHub.dao.UserFollowingDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.TechieTroveHub.pojo.constant.UserConstant.*;

/**
 * ClassName: UserFollowingService
 * Description:
 *
 * @Author agility6
 * @Create 2024/3/16 15:55
 * @Version: 1.0
 */
@Service
public class UserFollowingService {

    @Autowired
    private UserFollowingDao userFollowingDao;

    @Autowired
    private FollowingGroupService followingGroupService;

    @Autowired
    private UserService userService;

    @Transactional // 先删除再更新
    public void addUserFollowings(UserFollowing userFollowing) {
        // 获取是否有指定分组
        Long groupId = userFollowing.getGroupId();

        // 如果没有指定，添加到默认分组
        if (groupId == null) {
            FollowingGroup followingGroup = followingGroupService.getByType(USER_FOLLOWING_GROUP_TYPE_DEFAULT);
            // 系统的默认三个分组是通过主键进行关联的
            userFollowing.setGroupId(followingGroup.getId());
        } else {
            FollowingGroup followingGroup = followingGroupService.getById(groupId);
            if (followingGroup == null) {
                throw new ConditionException("关注分组不存在！");
            }
        }

        // 判断关注的ID存不存在
        Long followingId = userFollowing.getFollowingId();
        User user = userService.getUserById(followingId);

        if (user == null) {
            throw new ConditionException("关注的用户不存在！");
        }

        // 删除关联关系，覆盖更新操作
        userFollowingDao.deleteUserFollowing(userFollowing.getUserId(), followingId);

        // 添加
        userFollowing.setCreateTime(new Date());
        userFollowingDao.addUserFollowing(userFollowing);
    }

    // TODO 获取用户关注列表
    public List<FollowingGroup> getUserFollowings(Long userId) {
        // 查询当前用户关注列表
        List<UserFollowing> list =  userFollowingDao.getUserFollowings(userId);

        // 通过关注列表获取关注用户的id（followingId属性）
        Set<Long> followingIdSet = list.stream().map(UserFollowing::getFollowingId).collect(Collectors.toSet());

        // 查询被关注者的信息

        List<UserInfo> userInfoList = new ArrayList<>();
        if (!followingIdSet.isEmpty()) {
            // 根据关注用户id列表查询出userInfoList
            userInfoList = userService.getUserInfoByUserIds(followingIdSet);
         }

        // 用户信息的匹配
        for (UserFollowing userFollowing : list) {
            for (UserInfo userInfo : userInfoList) {
                if (userFollowing.getFollowingId().equals(userInfo.getUserId())) {
                    userFollowing.setUserInfo(userInfo);
                }
            }
        }


        // 3. 将关注用户按关注分组进行分类
        List<FollowingGroup> result = new ArrayList<>();

        // 分组列表,根据用户id把相关的关注分组全部查询出来
        List<FollowingGroup> groupList =  followingGroupService.getByUserId(userId);

        // 3.1 全部关注分组
        FollowingGroup allGroup = new FollowingGroup();
        allGroup.setName(USER_FOLLOWING_GROUP_ALL_NAME);
        allGroup.setFollowingInfoList(userInfoList);
        result.add(allGroup);

        for (FollowingGroup group : groupList) {
            List<UserInfo> infoList = new ArrayList<>();
            // FollowingGroup和UserFollowing进行匹配
            for (UserFollowing userFollowing : list) {
                if (group.getId().equals(userFollowing.getGroupId())) {
                    infoList.add(userFollowing.getUserInfo());
                }
            }
            group.setFollowingInfoList(infoList);
            result.add(group);
        }
        return result;
    }

    // 第一步：获取当前用户的粉丝列表
    // 第二步：根据粉丝的用户id查询基本信息
    // 第三步：查询当前用户是否已经关注该粉丝
    public List<UserFollowing> getUserFans(Long userId) {

        List<UserFollowing> fanList = userFollowingDao.getUserFans(userId);

        // UserFollowing::getUserId获取粉丝的UserId
        Set<Long> fanIdSet = fanList.stream().map(UserFollowing::getUserId).collect(Collectors.toSet());
        List<UserInfo> userInfoList = new ArrayList<>();
        if (!fanIdSet.isEmpty()) {
            userInfoList = userService.getUserInfoByUserIds(fanIdSet);
        }


        // 获取当前用户关注的人
        List<UserFollowing> followingList = userFollowingDao.getUserFollowings(userId);

        // 查看粉丝列表中有没有我关注的人
        for (UserFollowing fan : fanList) {
            for (UserInfo userInfo : userInfoList) {
                if (fan.getUserId().equals(userInfo.getUserId())) {
                    // TODO getUserFollowings需要初始化吗？
                    userInfo.setFollowed(false); // 是否被关注初始化
                    fan.setUserInfo(userInfo);
                }
            }
            for (UserFollowing following : followingList) {
                // 获取当前用户关注的人的id等于当前粉丝id，互粉状态
                if (following.getFollowingId().equals(fan.getUserId())) {
                    fan.getUserInfo().setFollowed(true);
                }
            }
        }

        return fanList;


    }

    public Long addUserFollowingGroups(FollowingGroup followingGroup) {

        followingGroup.setCreateTime(new Date());
        followingGroup.setType(USER_FOLLOWING_GROUP_TYPE_USER);
        followingGroupService.addFollowingGroup(followingGroup);
        return followingGroup.getId();
    }

    public List<FollowingGroup> getUserFollowingGroups(Long userId) {
        return followingGroupService.getUserFollowingGroups(userId);
    }

    public List<UserInfo> checkFollowingStatus(List<UserInfo> userInfoList, Long userId) {
        // 当前用户已经关注了哪些用户
        List<UserFollowing> userFollowingList = userFollowingDao.getUserFollowings(userId);

        for (UserInfo userInfo : userInfoList) {
            userInfo.setFollowed(false);
            for (UserFollowing userFollowing : userFollowingList) {
                if (userFollowing.getFollowingId().equals(userInfo.getUserId())) {
                    userInfo.setFollowed(true);
                }
            }
        }

        return userInfoList;
    }
}
