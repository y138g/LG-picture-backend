package com.itgr.lgpicturebackend.controller;

import com.itgr.lgpicturebackend.annotaction.AuthCheck;
import com.itgr.lgpicturebackend.common.BaseResponse;
import com.itgr.lgpicturebackend.common.ResultUtils;
import com.itgr.lgpicturebackend.constant.UserConstant;
import com.itgr.lgpicturebackend.model.dto.picture.PictureUploadRequest;
import com.itgr.lgpicturebackend.model.entity.User;
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

@RestController
@RequestMapping("/picture")
@Slf4j
public class PictureController {

    @Resource
    private UserService userService;

    @Resource
    private PictureService pictureService;

    /**
     * 图片上传
     *
     * @param multipartFile        图片文件
     * @param pictureUploadRequest 自定义图片上传请求（用于区分新增还是修改）
     * @param request              请求
     * @return 图片
     */
    @PostMapping("/upload")
    @AuthCheck(mustRole = UserConstant.GL_USER_ROLE)
    public BaseResponse<PictureVO> upload(
            @RequestPart("file") MultipartFile multipartFile,
            PictureUploadRequest pictureUploadRequest, HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        PictureVO pictureVO = pictureService.uploadPicture(multipartFile, pictureUploadRequest, user);
        return ResultUtils.success(pictureVO);
    }

}
