package com.itgr.lgpicturebackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itgr.lgpicturebackend.exception.ErrorCode;
import com.itgr.lgpicturebackend.exception.ThrowUtils;
import com.itgr.lgpicturebackend.manager.FileManager;
import com.itgr.lgpicturebackend.mapper.PictureMapper;
import com.itgr.lgpicturebackend.model.dto.file.UploadPictureResult;
import com.itgr.lgpicturebackend.model.dto.picture.PictureUploadRequest;
import com.itgr.lgpicturebackend.model.entity.Picture;
import com.itgr.lgpicturebackend.model.entity.User;
import com.itgr.lgpicturebackend.model.vo.PictureVO;
import com.itgr.lgpicturebackend.service.PictureService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author ygking
 * @description 针对表【picture(图片)】的数据库操作Service实现
 * @createDate 2024-12-18 20:26:09
 */
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
        implements PictureService {

    @Resource
    private FileManager fileManager;

    @Override
    public PictureVO uploadPicture(MultipartFile multipartFile,
                                   PictureUploadRequest pictureUploadRequest, User loginUser) {
        // 参数校验（不允许未登录用户调用）
        ThrowUtils.throwIf(null == loginUser, ErrorCode.NO_AUTH_ERROR, "未登录");
        // 判断是新增还是更新
        Long pictureId = null;
        if (null != pictureUploadRequest) {
            pictureId = pictureUploadRequest.getId();
        }
        // 如果是更新，判断图片是否存在
        if (null != pictureId) {
            boolean exists = this.lambdaQuery().eq(Picture::getId, pictureId).exists();
            ThrowUtils.throwIf(!exists, ErrorCode.PARAMS_ERROR, "图片不存在");
        }
        String uploadPathPrefix = "public/" + loginUser.getUserAccount();
        // 上传图片，得到图片信息
        UploadPictureResult uploadPictureResult = fileManager.upLoadPicture(multipartFile, uploadPathPrefix);
        // 构造要入库的图片信息
        Picture picture = getPicture(loginUser, pictureId, uploadPictureResult);
        // 新增
        boolean sign = this.saveOrUpdate(picture);
        ThrowUtils.throwIf(!sign, ErrorCode.SYSTEM_ERROR, "图片上传异常");
        return PictureVO.objToVo(picture);
    }

    /**
     * 图片赋值
     *
     * @param loginUser           登录用户
     * @param pictureId           图片id
     * @param uploadPictureResult 图片上传结果
     * @return 图片
     */
    private static Picture getPicture(User loginUser, Long pictureId, UploadPictureResult uploadPictureResult) {
        Picture picture = new Picture();
        picture.setId(pictureId);
        picture.setUrl(uploadPictureResult.getUrl());
        picture.setName(uploadPictureResult.getPicName());
        picture.setPicSize(uploadPictureResult.getPicSize());
        picture.setPicWidth(uploadPictureResult.getPicWidth());
        picture.setPicHeight(uploadPictureResult.getPicHeight());
        picture.setPicScale(uploadPictureResult.getPicScale());
        picture.setPicFormat(uploadPictureResult.getPicFormat());
        picture.setUserId(loginUser.getId());

        // 操作数据库，如果 pictureId 不为空，则为更新；反之，则为新增
        if (null != pictureId) {
            // 更新
            picture.setId(pictureId);
            picture.setEditTime(new Date());
        }
        return picture;
    }
}




