package com.TechieTroveHub.dao;

import com.TechieTroveHub.pojo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ClassName: VideoDao
 * Description:
 *
 * @Author agility6
 * @Create 2024/3/30 14:13
 * @Version: 1.0
 */
@Mapper
public interface VideoDao {

    Integer addVideos(Video video);

    Integer batchAddVideoTags(List<VideoTag> videoTagList);

    Integer pageCountVideos(Map<String, Object> params);

    List<Video> pageListVideos(Map<String, Object> params);

    Video getVideoById(Long Id);

    VideoLike getVideoLikeByVideoIdAndUserId(@Param("videoId") Long videoId, @Param("userId") Long userId);

    Integer addVideoLike(VideoLike videoLike);

    Integer deleteVideoLike(@Param("videoId") Long videoId, @Param("userId") Long userId);

    Long getVideoLikes(Long videoId);

    Integer deleteVideoCollection(@Param("videoId") Long videoId, @Param("userId") Long userId);

    Integer addVideoCollection(VideoCollection videoCollection);

    Long getVideoCollections(Long videoId);

    VideoCollection getVideoCollectionByVideoIdAndUserId(@Param("videoId") Long videoId, @Param("userId") Long userId);

    VideoCoin getVideoCoinByVideoIdAndUserId(@Param("videoId") Long videoId, @Param("userId") Long userId);

    Integer addVideoCoin(VideoCoin videoCoin);

    Integer updateVideoCoin(VideoCoin videoCoin);

    Long getVideoCoinsAmount(Long videoId);

    Integer addVideoComment(VideoComment videoComment);

    Integer pageCountVideoComments(Map<String, Object> params);

    List<VideoComment> pageListVideoComments(Map<String, Object> params);

    List<VideoComment> batchGetVideoCommentsByRootIds(@Param("rootIdList") List<Long> parentIdList);

    VideoView getVideoView(Map<String, Object> params);

    Integer addVideoView(VideoView videoView);

    Integer getVideoViewCounts(Long videoId);

    List<UserPreference> getAllUserPreference();

    List<Video> batchGetVideosByIds(@Param("idList") List<Long> itemIds);

    Integer batchAddVideoBinaryPictures(@Param("pictureList") List<VideoBinaryPicture> pictures);

    List<VideoBinaryPicture> getVideoBinaryImages(Map<String, Object> params);

    List<VideoViewCount> getVideoViewCountByVideoIds(Set<Long> videoIdSet);

    List<VideoDanmuCount> getVideoDanmuCountByVideoIds(Set<Long> videoIdSet);

    void updateVideoCollection(VideoCollection videoCollection);

    List<VideoTag> getVideoTagsByVideoId(Long videId);

    Integer deleteVideoTags(@Param("tagIdList") List<Long> tagIdList, @Param("videoId") Long videoId);
}
