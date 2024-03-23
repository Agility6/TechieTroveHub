package com.TechieTroveHub.dao;

import com.TechieTroveHub.pojo.UserMoment;
import org.apache.ibatis.annotations.Mapper;

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
}
