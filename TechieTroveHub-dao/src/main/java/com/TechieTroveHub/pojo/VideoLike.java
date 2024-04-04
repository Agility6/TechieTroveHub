package com.TechieTroveHub.pojo;

import java.util.Date;

/**
 * ClassName: VideoLike
 * Description:
 *
 * @Author agility6
 * @Create 2024/4/1 19:30
 * @Version: 1.0
 */
public class VideoLike {

    private Long id;

    private Long userId;

    private Long videoId;

    private Date createTime;

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

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
