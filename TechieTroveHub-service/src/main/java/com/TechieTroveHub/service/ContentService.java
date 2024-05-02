package com.TechieTroveHub.service;

import com.TechieTroveHub.dao.ContentDao;
import com.TechieTroveHub.pojo.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ClassName: ContentService
 * Description:
 *
 * @Author agility6
 * @Create 2024/4/30 14:38
 * @Version: 1.0
 */
@Service
public class ContentService {

    @Autowired
    private ContentDao contentDao;

    public Long addContent(Content content) {
        contentDao.addContent(content);
        return content.getId();
    }
}
