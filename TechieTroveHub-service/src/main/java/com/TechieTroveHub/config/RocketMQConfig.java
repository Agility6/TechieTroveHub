package com.TechieTroveHub.config;

import com.TechieTroveHub.POJO.UserFollowing;
import com.TechieTroveHub.POJO.UserMoment;
import com.TechieTroveHub.POJO.constant.UserMomentsConstant;
import com.TechieTroveHub.service.UserFollowingService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.netty.util.internal.StringUtil;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static com.TechieTroveHub.POJO.constant.UserMomentsConstant.GROUP_MOMENTS;
import static com.TechieTroveHub.POJO.constant.UserMomentsConstant.TOPIC_MOMENTS;

/**
 * ClassName: RocketMQConfig
 * Description:
 *
 * @Author agility6
 * @Create 2024/3/18 17:26
 * @Version: 1.0
 */
//@Configuration
public class RocketMQConfig {

    @Value("${rocketmq.name.server.address}")
    private String nameServerAddr;


    // TODO redis
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserFollowingService userFollowingService;


    @Bean("momentsProducer")
    public DefaultMQProducer momentsProducer() throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer(GROUP_MOMENTS);
        producer.setNamesrvAddr(nameServerAddr);
        producer.start();
        return producer;
    }

    @Bean("momentsConsumer")
    public DefaultMQPushConsumer momentsConsumer() throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(GROUP_MOMENTS);
        consumer.setNamesrvAddr(nameServerAddr);
        consumer.subscribe(TOPIC_MOMENTS, "*");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                MessageExt msg = msgs.get(0); // 获取第一个
                if (msg == null) { // 没有直接返回
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
                // 获取byte数组
                String bodyStr = new String(msg.getBody());
                // 获取对应的实体类
                UserMoment userMoment = JSONObject.toJavaObject(JSONObject.parseObject(bodyStr), UserMoment.class);
                // 获取userId，获取该userId的粉丝
                Long userId = userMoment.getId();
                List<UserFollowing> fanList = userFollowingService.getUserFans(userId);

                for (UserFollowing fan : fanList) { // 将消息推送到每一个用户中
                    // 使用redis
                    String key = "subscribed-" + fan.getUserId();
                    String subscribedListStr = redisTemplate.opsForValue().get(key);
                    List<UserMoment> subscribedList;
                    if (StringUtil.isNullOrEmpty(subscribedListStr)) { // 判断列表是否为null
                        // 生成新的空列表
                         subscribedList = new ArrayList<>();
                    } else {
                        subscribedList = JSONArray.parseArray(subscribedListStr, UserMoment.class);
                    }
                    subscribedList.add(userMoment);
                    // 存入redis
                    redisTemplate.opsForValue().set(key, JSONObject.toJSONString(subscribedList));
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        return consumer;
    }
}
