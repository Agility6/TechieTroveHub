package com.TechieTroveHub.service;

import com.TechieTroveHub.dao.TagDao;
import com.TechieTroveHub.pojo.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ClassName: TagService
 * Description:
 *
 * @Author agility6
 * @Create 2024/5/1 15:39
 * @Version: 1.0
 */
@Service
public class TagService {

    @Autowired
    private TagDao tagDao;

    public Long addTag(Tag tag) {
        tagDao.addTag(tag);
        return tag.getId();
    }
}
