package com.TechieTroveHub.dao;

import com.TechieTroveHub.pojo.File;
import org.apache.ibatis.annotations.Mapper;


/**
 * ClassName: FileDao
 * Description:
 *
 * @Author agility6
 * @Create 2024/3/25 20:55
 * @Version: 1.0
 */
@Mapper
public interface FileDao {
    File getFileByMD5(String md5);

    Integer addFile(File dbFileMD5);
}
