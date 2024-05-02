package com.TechieTroveHub.service;

import com.TechieTroveHub.pojo.*;
import com.TechieTroveHub.dao.UserMomentsDao;
import com.TechieTroveHub.pojo.constant.UserMomentsConstant;
import com.TechieTroveHub.utils.RocketMQUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static com.TechieTroveHub.pojo.constant.UserMomentsConstant.TOPIC_MOMENTS;

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

    @Autowired
    private UserService userService;

    @Autowired
    private VideoService videoService;

    @Value("${fdfs.http.storage-addr}")
    private String fastdfsUrl;

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

    // TODO 待查看
    public PageResult<UserMoment> pageListMoments(Integer size, Integer no, Long userId, String type) {

        Map<String, Object> params = new HashMap<>();
        params.put("start", (no - 1)*size);
        params.put("limit", size);
        params.put("userId", userId);
        params.put("type", type);

        Integer total = userMomentsDao.pageCountMoments(params);
        List<UserMoment> list = new ArrayList<>();

        if (total > 0) {
            // TODO
            list = userMomentsDao.pageListMoments(params);
            if (!list.isEmpty()) {
                // 处理不同类型的动态

                // 处理视频动态类型
                this.processVideoMoment(list.stream()
                        .filter(item -> UserMomentsConstant.TYPE_VIDEO
                                .equals(item.getType())).collect(Collectors.toList()));

                // 处理照片动态类型
                this.processImgMoment(list.stream()
                        .filter(item -> UserMomentsConstant.TYPE_IMG
                                .equals(item.getType())).collect(Collectors.toList()));

                // 匹配对应用户信息
                Set<Long> userIdSet = list.stream()
                        .map(UserMoment::getUserId).collect(Collectors.toSet());
                List<UserInfo> userInfoList = userService.getUserInfoByUserIds(userIdSet);

                list.forEach(moment -> userInfoList.forEach(userInfo -> {
                    if (moment.getUserId().equals(userInfo.getUserId())) {
                        moment.setUserInfo(userInfo);
                    }
                }));
            }
        }

        return new PageResult<>(total, list);
    }


    // TODO 待查看
    private void processImgMoment(List<UserMoment> list) {
        list.forEach(moment -> {
            // TODO UserMoment --> DTO
            Content content = moment.getContent();
            // TOOD ImgContent
            ImgContent contentDetail = content.getContentDetail().toJavaObject(ImgContent.class);
            contentDetail.setImg(fastdfsUrl + contentDetail.getImg());
            content.setContentDetail(JSONObject.parseObject(JSONObject.toJSONString(contentDetail)));
            moment.setContent(content);
        });
    }

    private void processVideoMoment(List<UserMoment> list) {
        List<Video> videoList = list.stream()
                .map(UserMoment::getContent)
                .map(content -> content.getContentDetail().toJavaObject(Video.class))
                .collect(Collectors.toList());
        List<Video> newVideoList = videoService.getVideoCount(videoList);
        newVideoList.forEach(video -> video.setThumbnail(fastdfsUrl+video.getThumbnail()));

        list.forEach(moment -> newVideoList.forEach(video -> {
            if (video.getId().equals(moment.getContent().getContentDetail().getLong("id"))) {
                JSONObject contentDetail = JSONObject.parseObject(JSONObject.toJSONString(video));
                moment.getContent().setContentDetail(contentDetail);
            }
        }));
    }
}
