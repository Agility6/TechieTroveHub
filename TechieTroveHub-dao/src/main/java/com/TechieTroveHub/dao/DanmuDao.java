package com.TechieTroveHub.dao;

import com.TechieTroveHub.pojo.Danmu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * ClassName: DanmuDao
 * Description:
 *
 * @Author agility6
 * @Create 2024/4/4 14:35
 * @Version: 1.0
 */
@Mapper
public interface DanmuDao {
    Integer addDanmu(Danmu danmu);

    List<Danmu> getDanmus(Map<String,Object> params);
}
