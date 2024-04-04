package com.TechieTroveHub.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * ClassName: UserCoinDao
 * Description:
 *
 * @Author agility6
 * @Create 2024/4/2 20:32
 * @Version: 1.0
 */
@Mapper
public interface UserCoinDao {
    Integer getUserCoinsAmount(Long userId);

    Integer updateUserCoinAmount(@Param("userId") Long userId, @Param("amount") int amount, @Param("updateTime") Date updateTime);
}
