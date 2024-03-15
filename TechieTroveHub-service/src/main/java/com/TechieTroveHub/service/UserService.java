package com.TechieTroveHub.service;

import com.TechieTroveHub.POJO.User;
import com.TechieTroveHub.POJO.UserInfo;
import com.TechieTroveHub.POJO.exception.ConditionException;
import com.TechieTroveHub.dao.UserDao;
import com.TechieTroveHub.utils.MD5Util;
import com.TechieTroveHub.utils.RSAUtil;
import com.TechieTroveHub.utils.TokenUtil;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.TechieTroveHub.POJO.constant.UserConstant.*;

/**
 * ClassName: UserService
 * Description:
 *
 * @Author agility6
 * @Create 2024/3/15 10:51
 * @Version: 1.0
 */
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public void addUser(User user) {

        String phone = user.getPhone();

        if (StringUtils.isNullOrEmpty(phone)) {
            throw new ConditionException("手机号不能为空！");
        }

        // TODO 判断手机号合法性

        User dbUser = this.getUserByPhone(phone);
        if (dbUser != null) {
            throw new ConditionException("该手机号已经被注册！");
        }

        // 注册逻辑
        // 1. 获取时间戳
        Date now = new Date();
        // 2. 生成盐值用于加密
        String salt = String.valueOf(now.getTime());
        // 3. 获取密码
        String password = user.getPassword();
        // 4. 密码解密
        String rawPassword;
        try {
            rawPassword = RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionException("密码解密失败！");
        }

        // 插入数据库之前MD5加密密码
        String md5Password = MD5Util.sign(rawPassword, salt, "UTF-8");

        // TODO 没有添加Email？
        user.setSalt(salt);
        user.setPassword(md5Password);
        user.setCreateTime(now);
        userDao.addUser(user);

        // 关联user_info
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setNick(DEFAULT_NICK);
        userInfo.setBirth(DEFAULT_BIRTH);
        userInfo.setGender(GENDER_MALE);
        userInfo.setCreatTime(now);
        userDao.addUserInfo(userInfo);
    }

    public User getUserByPhone(String phone) {
        return userDao.getUserByPhone(phone);
    }

    public String login(User user) {

        String phone = user.getPhone();

        if (StringUtils.isNullOrEmpty(phone)) {
            throw new ConditionException("手机号不能为空！");
        }

        // TODO 判断手机号合法性

        User dbUser = getUserByPhone(phone);
        if (dbUser == null) {
            throw new ConditionException("当前用户不存在！");
        }

        String password = user.getPassword();
        String rawPassword;
        try {
            rawPassword = RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionException("密码解密失败！");
        }

        String salt = dbUser.getSalt();
        String md5Password = MD5Util.sign(rawPassword, salt, "UTF-8");

        if (md5Password.equals(dbUser.getPassword())) {
            throw new ConditionException("密码错误！");
        }

        TokenUtil tokenUtil = new TokenUtil();

        return tokenUtil.generateToken(dbUser.getId());
    }
}
