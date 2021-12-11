package com.changgou.user.controller;

import com.changgou.file.FastDFSFile;
import com.changgou.util.FastDFSUtil;
import entity.Result;
import entity.StatusCode;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: HeWei·Yuan
 * @CreateTime: 2021-04-20 16:04
 * @Description:
 */
@RestController
@RequestMapping(value = "upload")
@CrossOrigin
public class FileUploadController {

    @PostMapping
    public Result upload(@RequestBody MultipartFile file) throws Exception {
        FastDFSFile fastDFSFile = new FastDFSFile(
                file.getOriginalFilename(),
                file.getBytes(),
                StringUtils.getFilenameExtension(file.getOriginalFilename())
        );
        String[] uploads = FastDFSUtil.upload(fastDFSFile);
        String url = "http://192.168.211.132:8080/" + uploads[0] + "/" + uploads[1];

        return new Result(true, StatusCode.OK, "文件上传成功！", url);
    }

}
