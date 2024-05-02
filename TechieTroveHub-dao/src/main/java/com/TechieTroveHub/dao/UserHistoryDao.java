package com.TechieTroveHub.dao;

import com.TechieTroveHub.pojo.UserVideoHistory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * ClassName: UserHistoryDao
 * Description:
 *
 * @Author agility6
 * @Create 2024/5/2 17:45
 * @Version: 1.0
 */
@Mapper
public interface UserHistoryDao {
    int pageCountUserVideoHistory(Map<String, Object> params);

    List<UserVideoHistory> pageListUserVideoHistory(Map<String, Object> params);
}
