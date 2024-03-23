package com.TechieTroveHub.pojo;

import java.util.Date;
import java.util.List;

/**
 * ClassName: FollowingGroup
 * Description:
 *
 * @Author agility6
 * @Create 2024/3/16 15:52
 * @Version: 1.0
 */
public class FollowingGroup {

    private Long id;

    private Long userId;

    private String name;

    private String type;

    private Date createTime;

    private Date updateTime;

    // TODO 待优化，全部关注者信息
    private List<UserInfo> followingInfoList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public List<UserInfo> getFollowingInfoList() {
        return followingInfoList;
    }

    public void setFollowingInfoList(List<UserInfo> followingInfoList) {
        this.followingInfoList = followingInfoList;
    }
}