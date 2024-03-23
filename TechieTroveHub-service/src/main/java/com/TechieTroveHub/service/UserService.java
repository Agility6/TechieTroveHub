package com.TechieTroveHub.service;

import com.TechieTroveHub.POJO.PageResult;
import com.TechieTroveHub.POJO.RefreshTokenDetail;
import com.TechieTroveHub.POJO.User;
import com.TechieTroveHub.POJO.UserInfo;
import com.TechieTroveHub.POJO.exception.ConditionException;
import com.TechieTroveHub.dao.UserDao;
import com.TechieTroveHub.utils.MD5Util;
import com.TechieTroveHub.utils.RSAUtil;
import com.TechieTroveHub.utils.TokenUtil;
import com.alibaba.fastjson.JSONObject;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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

    @Autowired
    private UserAuthService userAuthService;

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

        user.setSalt(salt);
        user.setPassword(md5Password);
        user.setCreateTime(now);
        userDao.addUser(user);

        // 关联user_info
        // TODO 应该增加事务 确保UserInfo和User同时成功
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setNick(DEFAULT_NICK);
        userInfo.setBirth(DEFAULT_BIRTH);
        userInfo.setGender(GENDER_MALE);
        userInfo.setCreateTime(now);
        userDao.addUserInfo(userInfo);


        // TODO 添加用户的默认权限
        userAuthService.addUserDefaultRole(user.getId());
    }

    public User getUserByPhone(String phone) {
        return userDao.getUserByPhone(phone);
    }

    public String login(User user) throws Exception {

        String phone = user.getPhone() == null ? "" : user.getPhone();
        String email = user.getEmail() == null ? "" : user.getEmail();

        if (StringUtils.isNullOrEmpty(phone) && StringUtils.isNullOrEmpty(email)) {
            throw new ConditionException("参数异常！");
        }

        // TODO 判断手机号合法性

        // 前端只会传手机号或者邮箱
        String phoneOrEmail = phone + email;

        User dbUser = userDao.getUserByPhoneOrEmail(phoneOrEmail);
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

        if (!md5Password.equals(dbUser.getPassword())) {
            throw new ConditionException("密码错误！");
        }

        return TokenUtil.generateToken(dbUser.getId());
    }

    public User getUserInfo(Long userId) {
       User user = userDao.getUserById(userId);
       UserInfo userInfo = userDao.getUserInfoByUserId(userId);
       // TODO 待优化user中嵌套了userInfo
       user.setUserInfo(userInfo);
       return user;
    }

    public void updateUsers(User user) throws Exception {
        Long id = user.getId();
        User dbUser= userDao.getUserById(id);
        if (dbUser == null) {
            throw new ConditionException("用户不存在！");
        }

        // TODO 判断是否是修改密码
        if (!StringUtils.isNullOrEmpty(user.getPassword())) {
            String rawPassword = RSAUtil.decrypt(user.getPassword());
            String md5Password = MD5Util.sign(rawPassword, dbUser.getSalt(), "UTF-8");
            user.setPassword(md5Password);
        }

        user.setUpdateTime(new Date());
        userDao.updateUsers(user);
    }

    public void updateUserInfos(UserInfo userInfo) {
        userInfo.setUpdateTime(new Date());
        userDao.updateUserInfos(userInfo);
    }

    public User getUserById(Long followingId) {
        return userDao.getUserById(followingId);
    }

    public List<UserInfo> getUserInfoByUserIds(Set<Long> usrIdList) {
        return userDao.getUserInfoByUserIds(usrIdList);
    }

    public PageResult<UserInfo> pageListUserInfos(JSONObject params) {
        Integer no = params.getInteger("no");
        Integer size = params.getInteger("size");
        params.put("start", (no - 1) * size);
        params.put("limit", size);
        Integer total =  userDao.pageCountUserInfos(params); // 查看符合用于信息的有多少

        List<UserInfo> list = new ArrayList<>();
        if (total > 0) {
            list = userDao.pageListUserInfos(params);
        }

        return new PageResult<>(total, list);
    }

    public Map<String, Object> loginForDts(User user) throws Exception {

        String phone = user.getPhone() == null ? "" : user.getPhone();
        String email = user.getEmail() == null ? "" : user.getEmail();

        if (StringUtils.isNullOrEmpty(phone) && StringUtils.isNullOrEmpty(email)) {
            throw new ConditionException("参数异常！");
        }

        // TODO 判断手机号合法性

        // 前端只会传手机号或者邮箱
        String phoneOrEmail = phone + email;

        User dbUser = userDao.getUserByPhoneOrEmail(phoneOrEmail);
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

        if (!md5Password.equals(dbUser.getPassword())) {
            throw new ConditionException("密码错误！");
        }

        String accessToken = TokenUtil.generateToken(dbUser.getId());
        // 刷新token
        String refreshToken = TokenUtil.generateRefreshToken(dbUser.getId());
        // 保存到数据库中
        userDao.deleteRefreshTokenByUserId(dbUser.getId());
        userDao.addRefreshToken(refreshToken, dbUser.getId(), new Date());
        Map<String, Object> result = new HashMap<>();
        result.put("accessToken", accessToken);
        result.put("refreshToken", refreshToken);
        return result;
    }

    public void logout(String refreshToken, Long userId) {
        userDao.deleteRefreshToken(refreshToken, userId);
    }

    public String refreshAccessToken(String refreshToken) throws Exception {
        RefreshTokenDetail refreshTokenDetail = userDao.getRefreshTokenDetail(refreshToken);
        if (refreshTokenDetail == null) {
            throw new ConditionException("555", "token过期！");
        }

        Long userId = refreshTokenDetail.getUserId();
        // 根据userId重新刷新token
        return TokenUtil.generateToken(userId);
    }
}
