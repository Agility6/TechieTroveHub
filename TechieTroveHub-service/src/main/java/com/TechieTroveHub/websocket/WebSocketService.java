package com.TechieTroveHub.websocket;

import com.TechieTroveHub.pojo.Danmu;
import com.TechieTroveHub.service.DanmuService;
import com.TechieTroveHub.utils.RocketMQUtil;
import com.TechieTroveHub.utils.TokenUtil;
import com.alibaba.fastjson.JSONObject;
import io.netty.util.internal.StringUtil;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.TechieTroveHub.pojo.constant.UserMomentsConstant.TOPIC_DANMUS;

/**
 * ClassName: WebSocketService
 * Description:
 *
 * @Author agility6
 * @Create 2024/4/4 14:12
 * @Version: 1.0
 */
@Component
@ServerEndpoint("/imserver/{token}")
public class WebSocketService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // 在线人数统计
    private static final AtomicInteger ONLINE_COUNT = new AtomicInteger(0);

    // 确保线程安全
    public static final ConcurrentHashMap<String, WebSocketService> WEBSOCKET_MAP = new ConcurrentHashMap<>();

    private static ApplicationContext APPLICATION_CONTEXT;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        WebSocketService.APPLICATION_CONTEXT = applicationContext;
    }

    private Session session;

    private String sessionId;

    private Long userId;

    /**
     * 打开连接
     * @param session
     * @param token
     */
    @OnOpen
    public void openConnection(Session session, @PathParam("token") String token) {

        try {
            // 记录userId
            userId = TokenUtil.verifyToken(token);
        } catch (Exception ignored) {}

        this.sessionId = session.getId();
        this.session = session;
        // 查看WEBSOCKET_MAP是否存在，存在则更新不存在则添加
        if (WEBSOCKET_MAP.containsKey(sessionId)) {
            WEBSOCKET_MAP.remove(sessionId);
            WEBSOCKET_MAP.put(sessionId, this);
        } else {
            WEBSOCKET_MAP.put(sessionId, this);
            ONLINE_COUNT.getAndIncrement();
        }

       logger.info("用户连接成功：" + sessionId + ", 当前在线人数：" + ONLINE_COUNT.get());

        try {
            this.sendMessage("0");
        } catch (Exception e) {
            logger.error("连接异常！");
        }
    }

    /**
     * 关闭连接
     */
    @OnClose
    public void closeConnection() {
        if (WEBSOCKET_MAP.containsKey(sessionId)) {
            WEBSOCKET_MAP.remove(sessionId);
            ONLINE_COUNT.getAndDecrement();
        }

        logger.info("用户退出：" + sessionId + "当前在线人数为：" + ONLINE_COUNT.get());
    }

    /**
     * 获取消息进行处理
     * @param message
     */
    @OnMessage
    public void onMessage(String message) {
        logger.info("用户信息：" + sessionId + ", 报文：" + message);
        if (!StringUtil.isNullOrEmpty(message)) {
            try {
                // 群发消息
                for (Map.Entry<String, WebSocketService> entry : WEBSOCKET_MAP.entrySet()) {
                    // 获取websocketService
                    WebSocketService webSocketService = entry.getValue();

                    DefaultMQProducer danmusProducer = (DefaultMQProducer) APPLICATION_CONTEXT.getBean("danmusProducer");
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("message", message);
                    jsonObject.put("sessionId", webSocketService.getSessionId());
                    Message msg = new Message(TOPIC_DANMUS, jsonObject.toJSONString().getBytes(StandardCharsets.UTF_8));
                    // 异步发送
                    RocketMQUtil.asyncSendMsg(danmusProducer, msg);
                }

                if (this.userId != null) {
                    // 将弹幕存入数据库
                    Danmu danmu = JSONObject.parseObject(message, Danmu.class);
                    danmu.setUserId(userId);
                    danmu.setCreateTime(new Date());
                    DanmuService danmuService = (DanmuService) APPLICATION_CONTEXT.getBean("danmuService");

                    // 异步保存
                    danmuService.asyncAddDanmu(danmu);

                    // 保存弹幕到redis中
                    danmuService.addDanmusToRedis(danmu);
                }

            } catch (Exception e) {
                logger.error("弹幕接受出现问题！");
                e.printStackTrace();
            }
        }
    }

    /**
     * 错误处理
     * @param error
     */
    @OnError
    public void onError(Throwable error) {

    }

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }


    /**
     * 定时任务5秒自动发送在线人数
     * @throws IOException
     */
    @Scheduled(fixedRate = 5000)
    private void noticeOnlineCount() throws IOException {
        for (Map.Entry<String, WebSocketService> entry : WebSocketService.WEBSOCKET_MAP.entrySet()) {
            WebSocketService webSocketService = entry.getValue();
            if (webSocketService.session.isOpen()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("onlineCount", ONLINE_COUNT.get());
                jsonObject.put("msg", "当前在线人数为" + ONLINE_COUNT.get());
                webSocketService.sendMessage(jsonObject.toJSONString());
            }
        }
    }

    public Session getSession() {
        return session;
    }

    public String getSessionId() {
        return sessionId;
    }
}
