package com.TechieTroveHub.pojo;

import com.alibaba.fastjson.JSONObject;

import java.util.Date;

/**
 * ClassName: Content
 * Description:
 *
 * @Author agility6
 * @Create 2024/4/30 14:39
 * @Version: 1.0
 */
public class Content {
    private Long id;

    private JSONObject contentDetail;

    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public JSONObject getContentDetail() {
        return contentDetail;
    }

    public void setContentDetail(JSONObject contentDetail) {
        this.contentDetail = contentDetail;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
