package com.TechieTroveHub.service;

import com.TechieTroveHub.dao.VideoDao;
import com.TechieTroveHub.pojo.*;
import com.TechieTroveHub.pojo.exception.ConditionException;
import com.TechieTroveHub.utils.FastDFSUtil;
import com.TechieTroveHub.utils.ImageUtil;
import com.TechieTroveHub.utils.IpUtil;
import eu.bitwalker.useragentutils.UserAgent;
import io.lettuce.core.ConnectionId;
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
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.File;
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
    private FileService fileService;

    @Autowired
    private ImageUtil imageUtil;

    @Autowired
    FastDFSUtil fastDFSUtil;


    private static final int DEFAULT_RECOMMEND_NUMBER = 3;

    private static final int FRAME_NO = 256;

    @Value("${fdfs.http.storage-addr}")
    private String fastdfsUrl;

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

    public void viewVideoOnlineBySlices(HttpServletRequest request, HttpServletResponse response, String url) throws Exception {
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
        Integer userCoinsAmount = userCoinService.getUserCoinsAmount(userId);

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
     *
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
     *
     * @param userId  用户id
     * @param itemId  参考内容id（根据该内容进行相似内容推荐）
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
        List<Long> itemIds = genericItemBasedRecommender.recommendedBecause(userId, itemId, howMany).stream().map(RecommendedItem::getItemID).collect(Collectors.toList());

        // 推荐视频
        return videoDao.batchGetVideosByIds(itemIds);
    }

    /**
     * 将用户偏好列表转换为一个适合协同过滤推荐算法使用的数据模型
     *
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

        for (List<UserPreference> userPreferences : list) { // 遍历每个用户的偏好列表
            // 为当前用户创建一个偏好数组，数组的大小为用户的偏好数量
            GenericPreference[] array = new GenericPreference[userPreferences.size()];
            for (int i = 0; i < userPreferences.size(); i++) { // 遍历当前用户的所有偏好
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

    /**
     * 提取黑白剪影图像，并上传至 FastDFS。
     *
     * @param videoId
     * @param fileMd5
     * @return
     * @throws Exception
     */
    public List<VideoBinaryPicture> convertVideoToImage(Long videoId, String fileMd5) throws Exception {
        // 将服务器视频下载到本地中
        com.TechieTroveHub.pojo.File file = fileService.getFileByMd5(fileMd5);
        // TODO file下载路径
        String filePath = "/Users/agility6/data/tmp" + videoId + "." + file.getType();
        // 下载文件
        fastDFSUtil.downLoadFile(file.getUrl(), filePath);

        // 创建一个默认的帧抓取器
        FFmpegFrameGrabber fFmpegFrameGrabber = FFmpegFrameGrabber.createDefault(filePath);
        // 启动帧抓取器
        fFmpegFrameGrabber.start();
        // 获取视频文件的帧数
        int ffLength = fFmpegFrameGrabber.getLengthInFrames();

        Frame frame;
        Java2DFrameConverter converter = new Java2DFrameConverter();
        int count = 1;
        List<VideoBinaryPicture> pictures = new ArrayList<>();

        for (int i = 1; i <= ffLength; i++) { // 通过循环遍历视频的每一帧
            // 获取当前帧的时间戳
            long timestamp = fFmpegFrameGrabber.getTimestamp();
            // 抓取当前帧的图像
            frame = fFmpegFrameGrabber.grabImage();
            // 检查是否需要处理的帧
            if (count == i) {
                if (frame == null) {
                    throw new ConditionException("无效帧");
                }

                // 将帧图像转化为BufferedImage
                BufferedImage bufferedImage = converter.getBufferedImage(frame);

                ByteArrayOutputStream os = new ByteArrayOutputStream();
                // 将其以 PNG 格式写入流中
                ImageIO.write(bufferedImage, "png", os);
                // 将流转换为输入流
                InputStream inputStream = new ByteArrayInputStream(os.toByteArray());

                // 创建临时文件，并将处理后的帧图像写入临时文件中
                java.io.File outputFile = java.io.File.createTempFile("convert-" + videoId + "-", ".png");
                // 获取图像的黑白剪影
                BufferedImage binaryImg = imageUtil.getBodyOutline(bufferedImage, inputStream);
                // 将黑白剪影图像写入临时文件中
                ImageIO.write(binaryImg, "png", outputFile);

                // 有的浏览器或网站需要把图片白色的部分转为透明色，使用以下方法可实现
                imageUtil.transferAlpha(outputFile, outputFile);

                // 上传处理后的剪影文件至 FastDFS，并获取其 URL
                String imgUrl = fastDFSUtil.uploadCommonFile(outputFile, "png");
                //  创建 VideoBinaryPicture 对象，设置相关属性，包括帧编号、URL、视频ID、视频时间戳
                VideoBinaryPicture videoBinaryPicture = new VideoBinaryPicture();
                videoBinaryPicture.setFrameNo(i);
                videoBinaryPicture.setUrl(imgUrl);
                videoBinaryPicture.setVideoId(videoId);
                videoBinaryPicture.setVideoTimestamp(timestamp);
                pictures.add(videoBinaryPicture);
                // 更新计数器 count，在下一次循环中处理下一个帧
                count += FRAME_NO;
                // 删除临时文件
                outputFile.delete();
            }
        }
        //删除临时文件
        File tmpFile = new File(filePath);
        tmpFile.delete();
        //批量添加视频剪影文件
        videoDao.batchAddVideoBinaryPictures(pictures);
        return pictures;
    }

    public List<VideoBinaryPicture> getVideoBinaryImages(Map<String, Object> params) {
        return videoDao.getVideoBinaryImages(params);
    }

    public List<Video> getVideoCount(List<Video> videoList) {

        if (!videoList.isEmpty()) {
            // 获取视频id集合
            Set<Long> videoIdSet = videoList.stream().map(Video::getId).collect(Collectors.toSet());

            // 统计播放量
            Map<Long, Integer> viewCountMap = this.batchCountVideoView(videoIdSet);
            // 统计弹幕量
            Map<Long, Integer> danmuCountMap = this.batchCountVideoDanmu(videoIdSet);

            // Video 增加了 播放量和弹幕量
            videoList.forEach(video -> {
                video.setViewCount(viewCountMap.get(video.getId()));
                video.setDanmuCount(danmuCountMap.get(video.getId()));
            });
        }
        return videoList;
    }

    // 统计视频播放量
    private Map<Long, Integer> batchCountVideoView(Set<Long> videoIdSet) {
        // VideoViewCount --> 新增类
        List<VideoViewCount> viewCount = videoDao.getVideoViewCountByVideoIds(videoIdSet);
        return viewCount.stream().collect(Collectors.toMap(VideoViewCount::getVideoId, VideoViewCount::getCount));
    }


    // 统计视频弹幕量
    private Map<Long, Integer> batchCountVideoDanmu(Set<Long> videoIdSet) {
        // VideoDanmuCount --> 新增类
        List<VideoDanmuCount> danmuCount = videoDao.getVideoDanmuCountByVideoIds(videoIdSet);
        return danmuCount.stream().collect(Collectors.toMap(VideoDanmuCount::getVideoId, VideoDanmuCount::getCount));
    }


    // TODO 待检查
    @Transactional
    public void updateVideoCollection(VideoCollection videoCollection, Long userId) {
        Long videoId = videoCollection.getVideoId();
        Long groupId = videoCollection.getGroupId();

        if (videoId == null || groupId == null) {
            throw new ConditionException("参数异常！");
        }

        // 修改Video信息
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("非法视频！");
        }
        videoCollection.setUserId(userId);
        videoDao.updateVideoCollection(videoCollection);
    }

    public List<VideoTag> getVideoTagsByVideoId(Long videId) {
        return videoDao.getVideoTagsByVideoId(videId);
    }

    public void deleteVideoTags(List<Long> tagIdList, Long videoId) {
        videoDao.deleteVideoTags(tagIdList, videoId);
    }

    public List<Video> getVisitorVideoRecommendations() {
        return this.pageListVideos(DEFAULT_RECOMMEND_NUMBER, 1, null).getList();
    }

    public List<Video> getVideoRecommendations(String recommendType, Long userId) {
        List<Video> list = new ArrayList<>();
        try {
            //根据推荐类型进行推荐：1基于用户推荐 2基于内容推荐
            if("1".equals(recommendType)){
                list = this.recommend(userId);
            }else{
                //找到用户最喜欢的视频，作为推荐的基础内容
                List<UserPreference> preferencesList = videoDao.getAllUserPreference();
                Optional<Long> itemIdOpt = preferencesList.stream().filter(item -> item.getUserId().equals(userId))
                        .max(Comparator.comparing(UserPreference :: getValue)).map(UserPreference::getVideoId);
                if(itemIdOpt.isPresent()){
                    list = this.recommendByItem(userId, itemIdOpt.get(), DEFAULT_RECOMMEND_NUMBER);
                }
            }
            //若没有计算出推荐内容，则默认查询最新视频
            if(list.isEmpty()){
                list = this.pageListVideos(3,1,null).getList();
            }else{
                list.forEach(video -> video.setThumbnail(fastdfsUrl+video.getThumbnail()));
            }
        }catch (Exception e){
            throw new ConditionException("推荐失败");
        }
        return list;
    }
}
