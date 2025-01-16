package com.itgr.lgpicturebackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itgr.lgpicturebackend.model.dto.picture.PictureUploadRequest;
import com.itgr.lgpicturebackend.model.entity.Picture;
import com.itgr.lgpicturebackend.model.entity.User;
import com.itgr.lgpicturebackend.model.vo.PictureVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author ygking
 * @description 针对表【picture(图片)】的数据库操作Service
 * @createDate 2024-12-18 20:26:09
 */
public interface PictureService extends IService<Picture> {

    /**
     * 上传图片
     *
     * @param multipartFile        图片
     * @param pictureUploadRequest 图片上传请求
     * @param loginUser            登录用户
     * @return 图片
     */
    PictureVO uploadPicture(MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest,
                            User loginUser);
}
