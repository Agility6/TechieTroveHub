package com.TechieTroveHub.dao;

import com.TechieTroveHub.pojo.UserMoment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * ClassName: UserMomentsDao
 * Description:
 *
 * @Author agility6
 * @Create 2024/3/21 21:43
 * @Version: 1.0
 */
@Mapper
public interface UserMomentsDao {
    Integer addUserMoments(UserMoment userMoment);

    Integer pageCountMoments(Map<String, Object> params);

    List<UserMoment> pageListMoments(Map<String, Object> params);
}
