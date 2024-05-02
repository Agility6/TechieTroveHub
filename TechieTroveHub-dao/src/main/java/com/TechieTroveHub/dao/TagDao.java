package com.TechieTroveHub.dao;

import com.TechieTroveHub.pojo.Tag;
import org.apache.ibatis.annotations.Mapper;

/**
 * ClassName: TagDao
 * Description:
 *
 * @Author agility6
 * @Create 2024/5/1 15:54
 * @Version: 1.0
 */
@Mapper
public interface TagDao {
    void addTag(Tag tag);
}
