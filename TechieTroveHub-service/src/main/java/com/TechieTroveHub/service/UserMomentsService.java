package com.TechieTroveHub.service;

import com.TechieTroveHub.POJO.UserMoment;
import com.TechieTroveHub.dao.UserMomentsDao;
import com.TechieTroveHub.utils.RocketMQUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import static com.TechieTroveHub.POJO.constant.UserMomentsConstant.TOPIC_MOMENTS;

/**
 * ClassName: UserMomentsService
 * Description:
 *
 * @Author agility6
 * @Create 2024/3/21 21:42
 * @Version: 1.0
 */
@Service
public class UserMomentsService {

    @Autowired
    private UserMomentsDao userMomentsDao;

    @Autowired
    private ApplicationContext applicationContext; // 引入上下文

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void addUserMoments(UserMoment userMoment) throws Exception {
        userMoment.setCreateTime(new Date());
        userMomentsDao.addUserMoments(userMoment);
        // 获取生产者
        DefaultMQProducer producer = (DefaultMQProducer) applicationContext.getBean("momentsProducer");
        Message msg = new Message(TOPIC_MOMENTS, JSONObject.toJSONString(userMoment).getBytes(StandardCharsets.UTF_8));
        // 发送方法
        RocketMQUtil.syncSendMsg(producer, msg);
    }

    public List<UserMoment> getUserSubscribedMoments(Long userId) {
        // 从redis中取出数据
        String key = "subscribed-" + userId;
        String listStr = redisTemplate.opsForValue().get(key);
        return JSONArray.parseArray(listStr, UserMoment.class);
    }
}
