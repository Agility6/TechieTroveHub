package com.TechieTroveHub.config;

import com.TechieTroveHub.pojo.UserFollowing;
import com.TechieTroveHub.pojo.UserMoment;
import com.TechieTroveHub.pojo.constant.UserMomentsConstant;
import com.TechieTroveHub.service.UserFollowingService;
import com.TechieTroveHub.websocket.WebSocketService;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.TechieTroveHub.pojo.constant.UserMomentsConstant.*;

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

    @Bean("danmusProducer")
    public DefaultMQProducer danmusProducer() throws Exception {
        // 实例化消息生产者Producer
        DefaultMQProducer producer = new DefaultMQProducer(GROUP_DANMUS);
        // 设置NameServer的地址
        producer.setNamesrvAddr(nameServerAddr);
        // 启动Producer实例
        producer.start();
        return producer;
    }

    // TODO ?
    @Bean("danmusConsumer")
    public DefaultMQPushConsumer danmusConsumer() throws Exception {
        // 实例化消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(GROUP_DANMUS);
        // 设置NameServer的地址
        consumer.setNamesrvAddr(nameServerAddr);
        // 订阅一个或者多个Topic，以及Tag来过滤需要消费的信息
        consumer.subscribe(TOPIC_DANMUS, "*");
        // 组册回调实现类来处理从broker拉取回来的信息
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                MessageExt msg = msgs.get(0);
                byte[] msgByte = msg.getBody();
                String bodyStr = new String(msgByte);
                JSONObject jsonObject = JSONObject.parseObject(bodyStr);
                String sessionId = jsonObject.getString("sessionId");
                String message = jsonObject.getString("message");
                WebSocketService webSocketService = WebSocketService.WEBSOCKET_MAP.get(sessionId);

                if (webSocketService.getSession().isOpen()) {
                    try {
                        webSocketService.sendMessage(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        // 启动消费者实例
        consumer.start();
        return consumer;
    }
}
