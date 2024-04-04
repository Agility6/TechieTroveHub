package com.TechieTroveHub.pojo;

import java.util.Date;

/**
 * ClassName: VideoCollection
 * Description:
 *
 * @Author agility6
 * @Create 2024/4/1 20:30
 * @Version: 1.0
 */
public class VideoCollection {

    private Long id;

    private Long videoId;

    private Long userId;

    private Long groupId;

    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
