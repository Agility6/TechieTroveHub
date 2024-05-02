package com.TechieTroveHub.api;

import com.TechieTroveHub.pojo.*;
import com.TechieTroveHub.service.ElasticSearchService;
import com.TechieTroveHub.service.VideoService;
import com.TechieTroveHub.support.UserSupport;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.map.HashedMap;
import org.apache.mahout.cf.taste.common.TasteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName: VideoApi
 * Description:
 *
 * @Author agility6
 * @Create 2024/3/30 14:10
 * @Version: 1.0
 */
@RestController
public class VideoApi {

    @Autowired
    private VideoService videoService;

    @Autowired
    private UserSupport userSupport;

    @Autowired
    ElasticSearchService elasticSearchService;

    /**
     * 视频投稿
     * @param video
     * @return
     */
    @PostMapping("/videos")
    public JsonResponse<String> addVideos(@RequestBody Video video) {
        Long userId = userSupport.getCurrentUserId();
        video.setUserId(userId);
        videoService.addVideos(video);
        // 在es中添加一条视频数据
        elasticSearchService.addVideo(video);
        return JsonResponse.success();

    }

    /**
     * 分页查询视频列表
     * @param size
     * @param no
     * @param area
     * @return
     */
    @GetMapping("/videos")
    public JsonResponse<PageResult<Video>> pageListVideos(Integer size, Integer no, String area) {
        PageResult<Video> result = videoService.pageListVideos(size, no, area);
        return new JsonResponse<>(result);
    }

    /**
     * 视频在线播放
     * @param request
     * @param response
     * @param url
     * @throws Exception
     */
    @GetMapping("/video-slices")
    public void viewVideoOnlineBySlices(HttpServletRequest request,
                                        HttpServletResponse response,
                                        String url) throws Exception {
        videoService.viewVideoOnlineBySlices(request, response, url);
    }

    /**
     * 视频点赞
     * @param videoId
     * @return
     */
    @PostMapping("/video-likes")
    public JsonResponse<String> addVideoLike(@RequestParam Long videoId) {
        Long userId = userSupport.getCurrentUserId();
        videoService.addVideoLike(videoId, userId);
        return JsonResponse.success();
    }

    /**
     * 取消视频点赞
     * @param videoId
     * @return
     */
    @DeleteMapping("/video-likes")
    public JsonResponse<String>  deleteVideoLike(@RequestParam Long videoId) {
        Long userId = userSupport.getCurrentUserId();
        videoService.deleteVideoLike(videoId, userId);
        return JsonResponse.success();
    }

    /**
     * 查询视频点赞数量
     * @param videoId
     * @return
     */
    @GetMapping("/video-likes")
    public JsonResponse<Map<String, Object>> getVideoLikes(@RequestParam Long videoId) {
        Long userId = null;
        try { // 如果是未登录的状态直接忽略即可
            userId = userSupport.getCurrentUserId();
        } catch (Exception ignored) {}

        Map<String, Object> result = videoService.getVideoLikes(videoId, userId);

        return new JsonResponse<>(result);
    }

    /**
     * 收藏视频
     * @param videoCollection
     * @return
     */
    @PostMapping("/video-collections")
    public JsonResponse<String> addVideoCollection(@RequestBody VideoCollection videoCollection) {
        Long userId = userSupport.getCurrentUserId();
        videoService.addVideoCollection(videoCollection, userId);
        return JsonResponse.success();
    }

    /**
     * 更新收藏视频
     * @param videoCollection
     * @return
     */
    @PutMapping("/video-collections")
    public JsonResponse<String> updateVideoCollection(@RequestBody VideoCollection videoCollection) {
        Long userId = userSupport.getCurrentUserId();
        videoService.updateVideoCollection(videoCollection, userId);
        return JsonResponse.success();
    }

    /**
     * 取消收藏视频
     * @param videoId
     * @return
     */
    @DeleteMapping("/video-collections")
    public JsonResponse<String> deleteVideoCollection(@RequestParam Long videoId) {
        Long userId = userSupport.getCurrentUserId();
        videoService.deleteVideoCollection(videoId, userId);
        return JsonResponse.success();
    }

    /**
     * 查询视频收藏数量
     * @param videoId
     * @return
     */
    @GetMapping("/video-collections")
    public JsonResponse<Map<String, Object>> getVideoCollections(@RequestParam Long videoId) {
        Long userId = null;
        try {
            userId = userSupport.getCurrentUserId();
        } catch (Exception ignored) {}
        Map<String, Object> result = videoService.getVideoCollections(videoId, userId);
        return new JsonResponse<>(result);
    }


    /**
     * 视频投币
     * @param videoCoin
     * @return
     */
    @PostMapping("/video-coins")
    public JsonResponse<String> addVideoCoins(@RequestBody VideoCoin videoCoin) {
        Long userId = userSupport.getCurrentUserId();
        videoService.addVideoCoins(videoCoin, userId);
        return JsonResponse.success();
    }

    /**
     * 查询视频投币数量
     * @param videoId
     * @return
     */
    @GetMapping("/video-coins")
    public JsonResponse<Map<String, Object>> getVideoCoins(@RequestParam Long videoId) {
        Long userId = null;
        try {
            userId = userSupport.getCurrentUserId();
        } catch (Exception ignored) {}
        Map<String, Object> result = videoService.getVideoCoins(videoId, userId);
        return new JsonResponse<>(result);
    }

    /**
     * 添加视频评论
     * @param videoComment
     * @return
     */
    @PostMapping("/video-comments")
    public JsonResponse<String> addVideoComment(@RequestBody VideoComment videoComment) {
        Long userId = userSupport.getCurrentUserId();
        videoService.addVideoComment(videoComment, userId);
        return JsonResponse.success();
    }

    /**
     * 分页查询视频评论
     * @param size
     * @param no
     * @param videoId
     * @return
     */
    @GetMapping("/video-comments")
    public JsonResponse<PageResult<VideoComment>> pageListVideoComments(@RequestParam Integer size,
                                                                        @RequestParam Integer no,
                                                                        @RequestParam Long videoId) {
        PageResult<VideoComment> result = videoService.pageListVideoComments(size, no, videoId);
        return new JsonResponse<>(result);
    }

    /**
     * 获取视频详情
     * @param videoId
     * @return
     */
    @GetMapping("/video-details")
    public JsonResponse<Map<String, Object>> getVideoDetails(@RequestParam Long videoId) {
        Map<String, Object> result = videoService.getVideoDetails(videoId);
        return new JsonResponse<>(result);
    }

    /**
     * 添加视频观看记录
     * @param videoView
     * @param request
     * @return
     */
    @PostMapping("/video-views")
    public JsonResponse<String> addVideoView(@RequestBody VideoView videoView,
                                             HttpServletRequest request) {
        Long userId;

        try {
            userId = userSupport.getCurrentUserId();
            videoView.setUserId(userId);
            videoService.addVideoView(videoView, request);
        } catch (Exception e) {
            videoService.addVideoView(videoView, request);
        }

        return JsonResponse.success();
    }

    /**
     * 查询视频播放量
     * @param videoId
     * @return
     */
    @GetMapping("/video-view-counts")
    public JsonResponse<Integer> getVideoViewCounts(@RequestParam Long videoId) {
        Integer count = videoService.getVideoViewCounts(videoId);
        return new JsonResponse<>(count);
    }

    /**
     * 视频内容推荐
     * @return
     * @throws TasteException
     */
    @GetMapping("/recommendations")
    public JsonResponse<List<Video>> recommend() throws TasteException {
        Long userId = userSupport.getCurrentUserId();
        List<Video> list = videoService.recommend(userId);
        return new JsonResponse<>(list);
    }

    /**
     * 视频帧截取生成黑白剪影
     * @param videoId
     * @param fileMd5
     * @return
     * @throws Exception
     */
    public JsonResponse<List<VideoBinaryPicture>> captureVideoFrame(@RequestParam Long videoId,
                                                                    @RequestParam String fileMd5) throws Exception {
        List<VideoBinaryPicture> list = videoService.convertVideoToImage(videoId, fileMd5);
        return new JsonResponse<>(list);
    }

    /**
     * 查询视频黑白剪影
     * @param videoId
     * @param videoTimestamp
     * @param frameNo
     * @return
     */
    public JsonResponse<List<VideoBinaryPicture>> getVideoBinaryImages(@RequestParam Long videoId,
                                                                       Long videoTimestamp,
                                                                       String frameNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("videoId", videoId);
        params.put("videoTimestamp", videoTimestamp);
        params.put("frameNo", frameNo);
        List<VideoBinaryPicture> list = videoService.getVideoBinaryImages(params);
        return new JsonResponse<>(list);
    }


    /**
     * 查询视频标签
     * TODO 待查看
     */
    @GetMapping("/video-tags")
    public JsonResponse<List<VideoTag>> getVideoTagsByVideoId(@RequestParam Long videId) {
        List<VideoTag> list = videoService.getVideoTagsByVideoId(videId);
        return new JsonResponse<>(list);
    }

    /**
     *  删除视频标签
     *  TODO待查看
     */
    @DeleteMapping("/video-tags")
    public JsonResponse<String> deleteVideoTags(@RequestBody JSONObject params) {
        String tagIdList = params.getString("tagIdList");
        Long videoId = params.getLong("videoId");
        videoService.deleteVideoTags(JSONArray.parseArray(tagIdList).toJavaList(Long.class), videoId);
        return JsonResponse.success();
    }

    /**
     * 视频内容推荐（游客版）
     */
    @GetMapping("/visitor-video-recommendations")
    public JsonResponse<List<Video>> getVisitorVideoRecommendations() {
        List<Video> list = videoService.getVisitorVideoRecommendations();
        return new JsonResponse<>(list);
    }

    /**
     * 视频内容推荐（整合版）
     * TODO 待查看
     */
    @GetMapping("/video-recommendations")
    public JsonResponse<List<Video>> getVideoRecommendations(@RequestParam String recommendType) {
        Long userId = userSupport.getCurrentUserId();
        List<Video> list = videoService.getVideoRecommendations(recommendType, userId);
        return new JsonResponse<>(list);
    }

}
