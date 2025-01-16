package com.itgr.lgpicturebackend.controller;

import com.itgr.lgpicturebackend.annotaction.AuthCheck;
import com.itgr.lgpicturebackend.common.BaseResponse;
import com.itgr.lgpicturebackend.common.ResultUtils;
import com.itgr.lgpicturebackend.constant.UserConstant;
import com.itgr.lgpicturebackend.manager.FileManager;
import com.itgr.lgpicturebackend.model.dto.file.UploadPictureResult;
import com.itgr.lgpicturebackend.model.dto.picture.PictureUploadRequest;
import com.itgr.lgpicturebackend.model.entity.User;
import com.itgr.lgpicturebackend.model.vo.LoginUserVO;
import com.itgr.lgpicturebackend.model.vo.PictureVO;
import com.itgr.lgpicturebackend.service.PictureService;
import com.itgr.lgpicturebackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

// 废除
@Deprecated
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Resource
    private FileManager fileManager;

    @Resource
    private UserService userService;

    @Resource
    private PictureService pictureService;

    /**
     * 测试文件上传
     *
     * @param multipartFile 文件
     * @return 可访问地址
     */
    @PostMapping("/test/upload")
    @AuthCheck(mustRole = UserConstant.GL_USER_ROLE)
    public BaseResponse<UploadPictureResult> testUpload(@RequestPart("file") MultipartFile multipartFile,
                                                        HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        LoginUserVO loginUserVO = userService.getLoginUserVO(user);
        // 文件路径以及文件名：/userAccount/uuid_ts_name.extension
        UploadPictureResult uploadPictureResult = fileManager
                .upLoadPicture(multipartFile, loginUserVO.getUserAccount());
        return ResultUtils.success(uploadPictureResult);
    }
    /**
     * 测试图片上传
     *
     * @param multipartFile 文件
     * @return 可访问地址
     */
    @PostMapping("/test/upload/picture")
    @AuthCheck(mustRole = UserConstant.GL_USER_ROLE)
    public BaseResponse<PictureVO> testUploadPicture(
            @RequestPart("file") MultipartFile multipartFile,
            PictureUploadRequest pictureUploadRequest, HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        PictureVO pictureVO = pictureService.uploadPicture(multipartFile, pictureUploadRequest, user);
        return ResultUtils.success(pictureVO);
    }

//    /**
//     * 测试文件下载
//     *
//     * @param filePath 文件路径
//     * @param response 响应
//     * @throws IOException 异常
//     */
//    @PostMapping("/test/download")
//    @AuthCheck(mustRole = UserConstant.GL_USER_ROLE)
//    public void testDownload(String filePath, HttpServletResponse response) throws IOException {
//        FileUtils.download(filePath, response, cosManager);
//    }
}
