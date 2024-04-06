package com.TechieTroveHub.service;

import com.TechieTroveHub.dao.VideoDao;
import com.TechieTroveHub.pojo.*;
import com.TechieTroveHub.pojo.exception.ConditionException;
import com.TechieTroveHub.utils.FastDFSUtil;
import com.TechieTroveHub.utils.IpUtil;
import eu.bitwalker.useragentutils.UserAgent;
import io.lettuce.core.ConnectionId;
import org.apache.commons.io.filefilter.ConditionalFileFilter;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.net.www.content.text.Generic;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ClassName: VideoService
 * Description:
 *
 * @Author agility6
 * @Create 2024/3/30 14:11
 * @Version: 1.0
 */
@Service
public class VideoService {


    @Autowired
    private VideoDao videoDao;

    @Autowired
    private UserCoinService userCoinService;

    @Autowired
    private UserService userService;

    @Autowired
    FastDFSUtil fastDFSUtil;

    @Transactional
    public void addVideos(Video video) {

        Date now = new Date();
        video.setCreateTime(new Date());

        videoDao.addVideos(video);
        // 获取视频id
        Long videoId = video.getId();

        // 获取视频标签列表
        List<VideoTag> tagList = video.getVideoTagList();
        tagList.forEach(item -> {
            item.setCreateTime(now);
            item.setVideoId(videoId);
        });

        videoDao.batchAddVideoTags(tagList);
    }

    public PageResult<Video> pageListVideos(Integer size, Integer no, String area) {
        if (size == null || no == null) {
            throw new ConditionException("参数异常！");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("start", (no - 1) * size);
        params.put("limit", size);
        params.put("area", area);
        List<Video> list = new ArrayList<>();

        Integer total = videoDao.pageCountVideos(params);

        if (total > 0) {
            list = videoDao.pageListVideos(params);
        }

        return new PageResult<>(total, list);
    }

    public void viewVideoOnlineBySlices(HttpServletRequest request,
                                        HttpServletResponse response,
                                        String url) throws Exception {
        fastDFSUtil.viewVideoOnlineBySlices(request, response, url);
    }

    public void addVideoLike(Long videoId, Long userId) {
        Video video = videoDao.getVideoById(videoId);

        if (video == null) {
            throw new ConditionException("非法视频！");
        }

        /**
         * 查询数据库是否存在该用户的点赞
         * 如果不存在或者未登录状态（userId=null）的情况，videoLike值为null
         */
        VideoLike videoLike = videoDao.getVideoLikeByVideoIdAndUserId(videoId, userId);

        if (videoLike != null) {
            throw new ConditionException("已经赞过！");
        }

        videoLike = new VideoLike();
        videoLike.setVideoId(videoId);
        videoLike.setUserId(userId);
        videoLike.setCreateTime(new Date());
        videoDao.addVideoLike(videoLike);
    }

    public void deleteVideoLike(Long videoId, Long userId) {
        videoDao.deleteVideoLike(videoId, userId);
    }

    public Map<String, Object> getVideoLikes(Long videoId, Long userId) {

        Long count = videoDao.getVideoLikes(videoId);
        VideoLike videoLike = videoDao.getVideoLikeByVideoIdAndUserId(videoId, userId);

        boolean like = videoLike != null;

        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("like", like); // 返回该用户是否已经like

        return result;
    }

    @Transactional
    public void addVideoCollection(VideoCollection videoCollection, Long userId) {

        // 获取视频id
        Long videoId = videoCollection.getVideoId();
        // 获取收藏组id
        Long groupId = videoCollection.getGroupId();

        if (videoId == null || groupId == null) {
            throw new ConditionException("参数异常！");
        }

        Video video = videoDao.getVideoById(videoId);

        if (video == null) {
            throw new ConditionException("非法视频！");
        }

        // 删除原有视频收藏
        videoDao.deleteVideoCollection(videoId, userId);

        // 添加新的视频收藏
        videoCollection.setUserId(userId);
        videoCollection.setCreateTime(new Date());
        videoDao.addVideoCollection(videoCollection);
    }

    public void deleteVideoCollection(Long videoId, Long userId) {
        videoDao.deleteVideoCollection(videoId, userId);
    }

    public Map<String, Object> getVideoCollections(Long videoId, Long userId) {
        // 获取该视频的收藏数
        Long count = videoDao.getVideoCollections(videoId);

        VideoCollection videoCollection = videoDao.getVideoCollectionByVideoIdAndUserId(videoId, userId);

        // TODO like or collection?
        boolean like = videoCollection != null;
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("like", like);

        return result;
    }

    @Transactional
    public void addVideoCoins(VideoCoin videoCoin, Long userId) {
        // 获取视频id
        Long videoId = videoCoin.getVideoId();
        // 获取投币数量
        Integer amount = videoCoin.getAmount();

        if (videoId == null) {
            throw new ConditionException("参数异常！");
        }

        // 获取视频
        Video video = videoDao.getVideoById(videoId);

        if (video == null) {
            throw new ConditionException("非法视频！");
        }

        // 查询当前登录用户是否拥有足够的硬币
        Integer userCoinsAmount =  userCoinService.getUserCoinsAmount(userId);

        userCoinsAmount = userCoinsAmount == null ? 0 : userCoinsAmount;

        if (amount > userCoinsAmount) { // 投币数大于拥护数
            throw new ConditionException("硬币数量不足！");
        }

        // 查询当前登录用户对该视频已经投了多少硬币
        VideoCoin dbVideoCoin = videoDao.getVideoCoinByVideoIdAndUserId(videoId, userId);

        if (dbVideoCoin == null) { // 如果为空则该视频没有被投币过
            videoCoin.setUserId(userId);
            videoCoin.setCreateTime(new Date());
            videoDao.addVideoCoin(videoCoin);
        } else {
            // 获取当前用户对视频的投币数
            Integer dbAmount = dbVideoCoin.getAmount();
            dbAmount += amount;
            // 更新投币数
            videoCoin.setUserId(userId);
            videoCoin.setAmount(dbAmount);
            videoCoin.setUpdateTime(new Date());
            videoDao.updateVideoCoin(videoCoin);
        }

        // 更新用户当前硬币总数
        userCoinService.updateUserCoinsAmount(userId, (userCoinsAmount - amount));

    }

    public Map<String, Object> getVideoCoins(Long videoId, Long userId) {
        // 获取当前视频的硬币数
        Long count = videoDao.getVideoCoinsAmount(videoId);

        // 获取当前用户对该视频的投币记录
        VideoCoin videoCoin = videoDao.getVideoCoinByVideoIdAndUserId(videoId, userId);

        boolean like = videoCoin != null;

        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("like", like);

        return result;



    }

    public void addVideoComment(VideoComment videoComment, Long userId) {

        // 获取videoId信息
        Long videoId = videoComment.getVideoId();
        if (videoId == null) {
            throw new ConditionException("参数异常！");
        }

        // 通过videoId获取videos
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("非法视频！");
        }

        videoComment.setUserId(userId);
        videoComment.setCreateTime(new Date());
        videoDao.addVideoComment(videoComment);

    }

    public PageResult<VideoComment> pageListVideoComments(Integer size, Integer no, Long videoId) {

        // 获取视频
        Video video = videoDao.getVideoById(videoId);

        if (video == null) {
            throw new ConditionException("非法视频！");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("start", (no - 1) * size);
        params.put("limit", size);
        params.put("videoId", videoId);

        Integer total = videoDao.pageCountVideoComments(params);

        List<VideoComment> list = new ArrayList<>();

        if (total > 0) {
            // 获取所有评论
            list = videoDao.pageListVideoComments(params);
            // 批量查询二级评论
            List<Long> parentIdList = list.stream().map(VideoComment::getId).collect(Collectors.toList());
            // 查询给定根节点评论ID列表中每个根节点评论的所有子评论
            // TODO ?
            List<VideoComment> childCommentList = videoDao.batchGetVideoCommentsByRootIds(parentIdList);
            // 获取用户id
            Set<Long> userIdList = list.stream().map(VideoComment::getUserId).collect(Collectors.toSet());
            // 获取二级评论的评论中用户id
            Set<Long> replyUserIdList = childCommentList.stream().map(VideoComment::getUserId).collect(Collectors.toSet());
            // 获取二级评论的回复用户id
            Set<Long> childUserIdList = childCommentList.stream().map(VideoComment::getReplyUserId).collect(Collectors.toSet());

            // 将评论和回复的用户添加到userIdList中
            userIdList.addAll(replyUserIdList);
            userIdList.addAll(childUserIdList);

            // 获取所有用户的信息
            List<UserInfo> userInfoList = userService.batchGetUserInfoByUserIds(userIdList);

            // 将userInfoList转化为Map
            Map<Long, UserInfo> userInfoMap = userInfoList.stream().collect(Collectors.toMap(UserInfo::getUserId, userInfo -> userInfo));

            list.forEach(comment -> {
                // 获取评论的id
                Long id = comment.getId();
                // 存储当前评论的子评论
                List<VideoComment> childList = new ArrayList<>();
                childCommentList.forEach(child -> {
                    // 如果当前子评论的根评论ID与当前评论的ID相匹配，则说明该子评论是当前评论的子评论
                    if (id.equals(child.getRootId())) {
                        // 从userInfoMap中获取对应的userInfo
                        // 为子评论设置评论者的用户信息
                        child.setUserInfo(userInfoMap.get(child.getUserId()));
                        // 为子评论设置回复对象的用户信息
                        child.setReplyUserInfo(userInfoMap.get(child.getReplyUserId()));
                        childList.add(child);
                    }
                });

                // 将子评论列表设置为当前评论的子评论列表
                comment.setChildList(childList);
                // 当前评论设置评论者的用户信息
                comment.setUserInfo(userInfoMap.get(comment.getUserId()));


            });
        }

        return new PageResult<>(total, list);
    }

    public Map<String, Object> getVideoDetails(Long videoId) {
        // 获取视频
        // TODO ?
        // Video video =  videoDao.getVideoDetails(videoId);
        Video video = videoDao.getVideoById(videoId);
        Long userId = video.getUserId();

        User user = userService.getUserInfo(userId);
        UserInfo userInfo = user.getUserInfo();

        Map<String, Object> result = new HashMap<>();
        result.put("video", video);
        result.put("userInfo", userInfo);

        return result;
    }

    public void addVideoView(VideoView videoView, HttpServletRequest request) {

        // 获取用户Id
        Long userId = videoView.getUserId();
        // 获取视频Id
        Long videoId = videoView.getVideoId();

        // 从HTTP请求头中获取User—Agent
        String agent = request.getHeader("User-Agent");
        
        UserAgent userAgent = UserAgent.parseUserAgentString(agent);

        // 将解析的userAgent作为clientId
        String clientId = String.valueOf(userAgent.getId());

        // 获取Ip
        String ip = IpUtil.getIP(request);

        Map<String, Object> params = new HashMap<>();

        if (userId != null) {
            params.put("userId", userId);
        } else {
            params.put("ip", ip);
            params.put("clientId", clientId);
        }

        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        params.put("today", sdf.format(now));
        params.put("videoId", videoId);

        // 获取观看记录
        VideoView dbVideoView = videoDao.getVideoView(params);
        if (dbVideoView == null) {
            videoView.setIp(ip);
            videoView.setClientId(clientId);
            videoView.setCreateTime(new Date());
            videoDao.addVideoView(videoView);
        }
    }

    public Integer getVideoViewCounts(Long videoId) {
        return videoDao.getVideoViewCounts(videoId);
    }

    /**
     * 基于用户的协同推荐
     * @param userId
     * @return
     */
    public List<Video> recommend(Long userId) throws TasteException {
        List<UserPreference> list = videoDao.getAllUserPreference();
        // 创建数据模型
        DataModel dataModel = this.createDataModel(list);
        // 获取用户相似程度
        UserSimilarity similarity = new UncenteredCosineSimilarity(dataModel);
        System.out.println(similarity.userSimilarity(11, 12));

        // 获取用户邻居，选择2作为用户邻居的数量
        UserNeighborhood userNeighborhood = new NearestNUserNeighborhood(2, similarity, dataModel);
        long[] ar = userNeighborhood.getUserNeighborhood(userId);

        // 构造推荐器
        Recommender recommender = new GenericUserBasedRecommender(dataModel, userNeighborhood, similarity);

        // 推荐视频
        List<RecommendedItem> recommendedItems = recommender.recommend(userId, 5);
        List<Long> itemIds = recommendedItems.stream().map(RecommendedItem::getItemID).collect(Collectors.toList());

        return videoDao.batchGetVideosByIds(itemIds);

    }

    /**
     * 基于内容的协同推荐
     * @param userId 用户id
     * @param itemId 参考内容id（根据该内容进行相似内容推荐）
     * @param howMany 需要推荐的数量
     * @return
     */
    public List<Video> recommendByItem(Long userId, Long itemId, int howMany) throws TasteException {
        List<UserPreference> list = videoDao.getAllUserPreference();

        // 创建数据模型
        DataModel dataModel = this.createDataModel(list);

        // 获取内容相似程度
        ItemSimilarity similarity = new UncenteredCosineSimilarity(dataModel);
        GenericItemBasedRecommender genericItemBasedRecommender = new GenericItemBasedRecommender(dataModel, similarity);
        // 物品推荐相似度，计算两个物品同时出现的次数，次数越多任务的相似度越高
        List<Long> itemIds = genericItemBasedRecommender.recommendedBecause(userId, itemId, howMany)
                .stream()
                .map(RecommendedItem::getItemID)
                .collect(Collectors.toList());

        // 推荐视频
        return videoDao.batchGetVideosByIds(itemIds);
    }

    /**
     * 将用户偏好列表转换为一个适合协同过滤推荐算法使用的数据模型
     * @param userPreferenceList
     * @return
     */
    private DataModel createDataModel(List<UserPreference> userPreferenceList) {

        // 快速根据用户ID查找偏好数组的数据结构
        FastByIDMap<PreferenceArray> fastByIdMap = new FastByIDMap<>();

        // 将用户偏好列表按照用户ID进行分组
        Map<Long, List<UserPreference>> map = userPreferenceList.stream().collect(Collectors.groupingBy(UserPreference::getUserId));

        // 获取分组后的所有用户偏好列表
        Collection<List<UserPreference>> list = map.values();

        for(List<UserPreference> userPreferences : list) { // 遍历每个用户的偏好列表
            // 为当前用户创建一个偏好数组，数组的大小为用户的偏好数量
            GenericPreference[] array = new GenericPreference[userPreferences.size()];
            for(int i = 0; i < userPreferences.size(); i++) { // 遍历当前用户的所有偏好
                // 获取当前偏好对象
                UserPreference userPreference = userPreferences.get(i);
                // 根据偏好对象创建一个通用的偏好对象，其中包括用户ID、视频ID和偏好值
                GenericPreference item = new GenericPreference(userPreference.getUserId(), userPreference.getVideoId(), userPreference.getValue());
                // 将偏好对象添加到当前用户的偏好数组中。
                array[i] = item;
            }
            // 将当前用户的偏好数组放入到fastByIdMap
            fastByIdMap.put(array[0].getUserID(), new GenericUserPreferenceArray(Arrays.asList(array)));
        }
        return new GenericDataModel(fastByIdMap);
    }
}
