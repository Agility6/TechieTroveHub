package com.TechieTroveHub.service;

import com.TechieTroveHub.dao.FileDao;
import com.TechieTroveHub.pojo.File;
import com.TechieTroveHub.utils.FastDFSUtil;
import com.TechieTroveHub.utils.MD5Util;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;


/**
 * ClassName: FileService
 * Description:
 *
 * @Author agility6
 * @Create 2024/3/25 20:42
 * @Version: 1.0
 */
@Service
public class FileService {

    @Autowired
    FileDao fileDao;

    @Autowired
    private FastDFSUtil fastDFSUtil;

    public String getFileMD5(MultipartFile file) throws Exception {
        return MD5Util.getFileMD5(file);
    }

    public String uploadFileBySlices(MultipartFile slice, String fileMD5, Integer sliceNo, Integer totalSliceNo) throws Exception {

        // 查看数据库是否存在相同文件
        File dbFileMD5 = fileDao.getFileByMD5(fileMD5);

        if (dbFileMD5 != null) { // 存在直接返回URL
            return dbFileMD5.getUrl();
        }

        // 将file上传返回url
        String url = fastDFSUtil.uploadFileBySlices(slice, fileMD5, sliceNo, totalSliceNo);
        // 保存到数据库中
        if (!StringUtil.isNullOrEmpty(url)) {
            dbFileMD5 = new File();
            dbFileMD5.setCreateTime(new Date());
            dbFileMD5.setMd5(fileMD5);
            dbFileMD5.setUrl(url);
            dbFileMD5.setType(fastDFSUtil.getFileType(slice));
            fileDao.addFile(dbFileMD5);
        }

        return url;
    }
}
