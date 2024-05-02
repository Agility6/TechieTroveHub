package com.TechieTroveHub.pojo;

/**
 * ClassName: VideoArea
 * Description:
 *
 * @Author agility6
 * @Create 2024/5/1 16:25
 * @Version: 1.0
 */
public class VideoArea {

    private Long userId;

    private String area;

    private Integer count;


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
