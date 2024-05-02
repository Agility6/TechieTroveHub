package com.TechieTroveHub.dao;

import com.TechieTroveHub.pojo.Content;
import org.apache.ibatis.annotations.Mapper;

/**
 * ClassName: ContentDao
 * Description:
 *
 * @Author agility6
 * @Create 2024/4/30 14:41
 * @Version: 1.0
 */
@Mapper
public interface ContentDao {
    Long addContent(Content content);
}
