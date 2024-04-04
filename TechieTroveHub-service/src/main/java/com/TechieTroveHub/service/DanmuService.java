package com.TechieTroveHub.service;

import com.TechieTroveHub.dao.DanmuDao;
import com.TechieTroveHub.pojo.Danmu;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * ClassName: DanmuService
 * Description:
 *
 * @Author agility6
 * @Create 2024/4/4 14:37
 * @Version: 1.0
 */
@Service
public class DanmuService {

    private static final String DANMU_KEY = "dm-video-";

    @Autowired
    private DanmuDao danmuDao;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    public void addDanmu(Danmu danmu) {
        danmuDao.addDanmu(danmu);
    }

    /**
     * 增加弹幕到redis中
     * @param danmu
     */
    public void addDanmusToRedis(Danmu danmu) {
        String key = DANMU_KEY + danmu.getVideoId();
        String value = redisTemplate.opsForValue().get(key);
        List<Danmu> list = new ArrayList<>();

        if (!StringUtil.isNullOrEmpty(value)) {
            list = JSONArray.parseArray(value, Danmu.class);
        }

        list.add(danmu);
        redisTemplate.opsForValue().set(key, JSONObject.toJSONString(list));
    }

    @Async
    public void asyncAddDanmu(Danmu danmu) {
        danmuDao.addDanmu(danmu);
    }

    /**
     * 查询策略是优先查redis中的弹幕数据，
     * 如果没有的话查询数据库，然后把查询的数据写入redis当中
     * @param videoId
     * @param startTime
     * @param endTime
     * @return
     */
    public List<Danmu> getDanmus(Long videoId, String startTime, String endTime) throws Exception {

        String key = DANMU_KEY + videoId;
        // 从redis中获取
        String value = redisTemplate.opsForValue().get(key);

        List<Danmu> list;

        if (!StringUtil.isNullOrEmpty(value)) { // 如果redis中存在
            list = JSONArray.parseArray(value, Danmu.class);
            if (!StringUtil.isNullOrEmpty(startTime)
            && !StringUtil.isNullOrEmpty(endTime)) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date startDate = sdf.parse(startTime);
                Date endDate = sdf.parse(endTime);
                // 给定的时间范围内筛选出符合条件的弹幕
                List<Danmu> childList = new ArrayList<>();

                for (Danmu danmu : list) {
                    Date createTime = danmu.getCreateTime();
                    if (createTime.after(startDate) && createTime.before(endDate)) {
                        childList.add(danmu);
                    }
                }

                list = childList;
            }
        } else { // redis中不存在弹幕
            Map<String, Object> params = new HashMap<>();
            params.put("videoId", videoId);
            params.put("startTime", startTime);
            params.put("endTime", endTime);
            // 从数据库中获取
            list = danmuDao.getDanmus(params);
            // 保存到redis中
            redisTemplate.opsForValue().set(key, JSONObject.toJSONString(list));
        }
        return list;
    }
}
