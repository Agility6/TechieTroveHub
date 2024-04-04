package com.TechieTroveHub.utils;

import com.TechieTroveHub.pojo.exception.ConditionException;
import com.github.tobato.fastdfs.domain.fdfs.FileInfo;
import com.github.tobato.fastdfs.domain.fdfs.MetaData;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.*;

/**
 * ClassName: FastDFSUtil
 * Description:
 *
 * @Author agility6
 * @Create 2024/3/24 21:12
 * @Version: 1.0
 */
@Configuration
public class FastDFSUtil {

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    @Autowired
    private AppendFileStorageClient appendFileStorageClient;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${fdfs.http.storage-addr}")
    private String httpFdfsStorageAddr;


    private static final String PATH_KEY = "path-key:";

    private static final String UPLOADED_SIZE_KEY = "uploaded-size-key:";

    private static final String UPLOADED_NO_KEY = "uploaded-no-key:";

    private static final String DEFAULT_GROUP = "group1";

    private static final int SLICE_SIZE = 1024 * 1024;


    /**
     * 获取文件类型
     * @param file
     * @return
     */
    public String getFileType(MultipartFile file) {
        if (file == null) {
            throw new ConditionException("非法文件！");
        }
        String fileName = file.getOriginalFilename();
        int index = fileName.lastIndexOf("."); // 获取最后一个"."
        return fileName.substring(index + 1);
    }

    // 上传
    public String uploadCommonFile(MultipartFile file) throws Exception {
        Set<MetaData> metaDataSet = new HashSet<>();
        String fileType = this.getFileType(file);
        StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), fileType, metaDataSet);
        return storePath.getPath();
    }

    // 删除
    public void deleteFile(String filePath) {
        fastFileStorageClient.deleteFile(filePath);
    }

    // 上传可以断点续传的文件
    public String uploadAppenderFile(MultipartFile file) throws Exception {
//        String fileType = this.getFileType(file);
//        StorePath storePath = appendFileStorageClient.uploadAppenderFile(DEFAULT_GROUP, file.getInputStream(), file.getSize(), fileType);
//        return storePath.getPath();
        String fileType = this.getFileType(file);
        StorePath storePath = appendFileStorageClient.uploadAppenderFile(DEFAULT_GROUP, file.getInputStream(), file.getSize(), fileType);
        return storePath.getPath();
    }

    /**
     *
     * @param file
     * @param filePath
     * @param offset 偏移量
     * @throws Exception
     */
    public void modifyAppenderFile(MultipartFile file, String filePath, long offset) throws Exception {
        appendFileStorageClient.modifyFile(DEFAULT_GROUP, filePath, file.getInputStream(), file.getSize(), offset);
    }

    /**
     *
     * @param file 文件
     * @param fileMd5 Md5加密
     * @param sliceNo 切片编号
     * @param totalSliceNo 切片的总数
     * @return
     */
    public String uploadFileBySlices(MultipartFile file, String fileMd5, Integer sliceNo, Integer totalSliceNo) throws Exception {
//        if (file == null || sliceNo == null || totalSliceNo == null) {
//            throw new ConditionException("参数异常！");
//        }
//
//        // 路径KEY
//        String pathKey = PATH_KEY + fileMd5;
//
//        // 已上传大小
//        String uploadedSizeKey = UPLOADED_SIZE_KEY + fileMd5;
//
//        // 已上传分片数
//        String uploadedNoKey = UPLOADED_NO_KEY + fileMd5;
//
//        // 获取已上传大小
//        String uploadedSizeStr = redisTemplate.opsForValue().get(uploadedSizeKey);
//
//        Long uploadedSize = 0L;
//
//        if (!StringUtil.isNullOrEmpty(uploadedSizeStr)) {
//            uploadedSize = Long.valueOf(uploadedSizeStr);
//        }
//
//        if (sliceNo == 1) { // 上传的是第一个分片特殊处理
//            String path = this.uploadAppenderFile(file);
//            if (StringUtil.isNullOrEmpty(path)) {
//                throw new ConditionException("上传失败！");
//            }
//            // 初始化
//            redisTemplate.opsForValue().set(pathKey, path);
//            redisTemplate.opsForValue().set(uploadedNoKey, "1");
//        } else {
//            String filePath = redisTemplate.opsForValue().get(pathKey);
//            if (StringUtil.isNullOrEmpty(filePath)) {
//                throw new ConditionException("上传失败！");
//            }
//            // 将当前分片追加到已上传的文件上
//            this.modifyAppenderFile(file, filePath, uploadedSize);
//            // 并更新Redis中记录的已上传分片数。
//            redisTemplate.opsForValue().increment(uploadedNoKey);
//        }
//
//        // 更新已经上传分片文件的总大小
//        uploadedSize += file.getSize();
//        redisTemplate.opsForValue().set(uploadedSizeKey, String.valueOf(uploadedSize));
//
//        // 如果所有分片全部上传完毕，则清空redis里面相关的key和value
//        String uploadedNoStr = redisTemplate.opsForValue().get(uploadedNoKey);
//        Integer uploadedNo = Integer.valueOf(uploadedNoStr);
//        String resultPath = "";
//
//        if (uploadedNo.equals(totalSliceNo)) {
//            resultPath = redisTemplate.opsForValue().get(pathKey);
//            // 清除Redis中与该文件上传任务相关的所有键值对。这包括已上传的分片数量、文件路径、以及已上传的总大小等信息
//            List<String> keyList = Arrays.asList(uploadedNoKey, pathKey, uploadedSizeKey);
//            redisTemplate.delete(keyList);
//        }
//
//        return resultPath;

        if(file == null || sliceNo == null || totalSliceNo == null){
            throw new ConditionException("参数异常！");
        }
        String pathKey = PATH_KEY + fileMd5;
        String uploadedSizeKey = UPLOADED_SIZE_KEY + fileMd5;
        String uploadedNoKey = UPLOADED_NO_KEY + fileMd5;
        String uploadedSizeStr = redisTemplate.opsForValue().get(uploadedSizeKey);
        Long uploadedSize = 0L;
        if(!StringUtil.isNullOrEmpty(uploadedSizeStr)){
            uploadedSize = Long.valueOf(uploadedSizeStr);
        }
        if(sliceNo == 1){ //上传的是第一个分片
            String path = this.uploadAppenderFile(file);
            if(StringUtil.isNullOrEmpty(path)){
                throw new ConditionException("上传失败！");
            }
            redisTemplate.opsForValue().set(pathKey, path);
            redisTemplate.opsForValue().set(uploadedNoKey, "1");
        }else{
            String filePath = redisTemplate.opsForValue().get(pathKey);
            if(StringUtil.isNullOrEmpty(filePath)){
                throw new ConditionException("上传失败！");
            }
            this.modifyAppenderFile(file, filePath, uploadedSize);
            redisTemplate.opsForValue().increment(uploadedNoKey);
        }
        // 修改历史上传分片文件大小
        uploadedSize  += file.getSize();
        redisTemplate.opsForValue().set(uploadedSizeKey, String.valueOf(uploadedSize));
        //如果所有分片全部上传完毕，则清空redis里面相关的key和value
        String uploadedNoStr = redisTemplate.opsForValue().get(uploadedNoKey);
        Integer uploadedNo = Integer.valueOf(uploadedNoStr);
        String resultPath = "";
        if(uploadedNo.equals(totalSliceNo)){
            resultPath = redisTemplate.opsForValue().get(pathKey);
            List<String> keyList = Arrays.asList(uploadedNoKey, pathKey, uploadedSizeKey);
            redisTemplate.delete(keyList);
        }
        return resultPath;
    }


    public void convertFileToSlices(MultipartFile multipartFile) throws Exception{
        // 文件名称
        String fileType = this.getFileType(multipartFile);
        //生成临时文件，将MultipartFile转为File
        File file = this.multipartFileToFile(multipartFile);
        // 文件长度
        long fileLength = file.length();
        int count = 1;
        for(int i = 0; i < fileLength; i += SLICE_SIZE){
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            randomAccessFile.seek(i);
            byte[] bytes = new byte[SLICE_SIZE];
            int len = randomAccessFile.read(bytes);
            String path = "/Users/agility6/data/tmp/" + count + "." + fileType;
            File slice = new File(path);
            FileOutputStream fos = new FileOutputStream(slice);
            fos.write(bytes, 0, len);
            fos.close();
            randomAccessFile.close();
            count++;
        }
        //删除临时文件
        file.delete();
    }


    /**
     * TODO DEBUG
     * multipartFile转化为File类型
     * @param multipartFile
     * @return
     * @throws Exception
     */
    public File multipartFileToFile(MultipartFile multipartFile) throws Exception{
        String originalFileName = multipartFile.getOriginalFilename();
        String[] fileName = originalFileName.split("\\.");
        // 生成临时文件
        File file = File.createTempFile(fileName[0], "." + fileName[1]);
        multipartFile.transferTo(file);
        return file;
    }

    public void viewVideoOnlineBySlices(HttpServletRequest request,
                                        HttpServletResponse response,
                                        String path) throws Exception {
        // 获取文件信息
        FileInfo fileInfo = fastFileStorageClient.queryFileInfo(DEFAULT_GROUP, path);

        // 获取文件总大小
        long totalFileSize = fileInfo.getFileSize();

        String url = httpFdfsStorageAddr + path;

        // 获取request
        Enumeration<String> headerNames = request.getHeaderNames();
        Map<String, Object> headers  = new HashMap<>();
        while (headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            headers.put(header, request.getHeader(header));
        }

        // 获取range范围信息 Range:bytes=4554752-41670075
        String rangeStr = request.getHeader("Range");
        String[] range;
        if (StringUtil.isNullOrEmpty(rangeStr)) {
            rangeStr = "bytes=0-" + (totalFileSize - 1);
        }

        // 分割 bytes=4554752-41670075
        range = rangeStr.split("bytes=|-");

        // 起始位置
        long begin = 0;

        if (range.length >= 2) { // 有开始位置
            begin = Long.parseLong(range[1]);
        }

        long end = 0;
        if (range.length >= 3) { // 有结束位置
            end = Long.parseLong(range[2]);
        }

        long len = (end - begin) + 1;


        // 构造相应头
        String contentRange = "bytes " + begin + "-" + end + "/" + totalFileSize;
        response.setHeader("Content-Range", contentRange);
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Content-Type", "video/mp4");
        response.setContentLength((int) len);
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);

        // 请求服务器中的视频
        HttpUtil.get(url, headers, response);
    }
}
