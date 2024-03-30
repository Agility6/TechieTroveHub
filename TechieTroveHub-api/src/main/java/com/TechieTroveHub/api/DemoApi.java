package com.TechieTroveHub.api;

import com.TechieTroveHub.utils.FastDFSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * ClassName: DemoApi
 * Description:
 *
 * @Author agility6
 * @Create 2024/3/26 19:00
 * @Version: 1.0
 */
@RestController
public class DemoApi {

    @Autowired
    private FastDFSUtil fastDFSUtil;

    @GetMapping("/slices")
    public void slices(MultipartFile file) throws Exception {
        fastDFSUtil.convertFileToSlices(file);
    }
}
