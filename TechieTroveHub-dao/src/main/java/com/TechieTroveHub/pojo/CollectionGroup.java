package com.TechieTroveHub.pojo;

/**
 * ClassName: CollectionGroup
 * Description:
 *
 * @Author agility6
 * @Create 2024/5/1 16:57
 * @Version: 1.0
 */
public class CollectionGroup {

    private Long groupId;

    private String groupName;

    private Integer count;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
