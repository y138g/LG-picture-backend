package com.itgr.lgpicturebackend.manager;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.RandomUtil;
import com.itgr.lgpicturebackend.config.CosClientConfig;
import com.itgr.lgpicturebackend.exception.BusinessException;
import com.itgr.lgpicturebackend.exception.ErrorCode;
import com.itgr.lgpicturebackend.exception.ThrowUtils;
import com.itgr.lgpicturebackend.model.dto.file.UploadPictureResult;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * @author y138g
 * 文件管理
 */
@Service
@Slf4j
public class FileManager {

    @Resource
    private CosManager cosManager;

    @Resource
    private CosClientConfig cosClientConfig;

    /**
     * 图片上传
     *
     * @param multipartFile    图片
     * @param uploadPathPrefix 上传路径前缀
     * @return 上传结果
     */
    public UploadPictureResult upLoadPicture(MultipartFile multipartFile, String uploadPathPrefix) {
        // 图片校验
        checkPicture(multipartFile);
        // 上传照片
        String filename = multipartFile.getOriginalFilename();
        // 组成上传文件路径（uuid_时间戳_名称.后缀）
        String uuid = RandomUtil.randomString(16);
        String uploadName = String.format("%s_%s_%s", uuid, DateUtil.formatDate(new Date()), filename);
        String filepath = String.format("%s/%s", uploadPathPrefix, uploadName);
        File file = null;
        try {
            // 上传文件
            file = File.createTempFile(filepath, null);
            multipartFile.transferTo(file);
            // 上传图片
            PutObjectResult putObjectResult = cosManager.putPictureObject(filepath, file);
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            // 封装返回结果
            UploadPictureResult uploadPictureResult = new UploadPictureResult();
            int picWidth = imageInfo.getWidth();
            int picHeight = imageInfo.getHeight();
            double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();
            uploadPictureResult.setPicName(FileUtil.mainName(filename));
            uploadPictureResult.setPicWidth(picWidth);
            uploadPictureResult.setPicHeight(picHeight);
            uploadPictureResult.setPicScale(picScale);
            uploadPictureResult.setPicFormat(imageInfo.getFormat());
            uploadPictureResult.setPicSize(FileUtil.size(file));
            uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + filepath);
            return uploadPictureResult;
        } catch (Exception e) {
            log.error("file upload error, filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            deleteTemporaryFile(file);
        }

    }

    /**
     * 图片校验
     *
     * @param file 图片
     */
    public void checkPicture(MultipartFile file) {
        // 校验入参
        ThrowUtils.throwIf(ObjUtil.isEmpty(file), ErrorCode.NOT_FOUND_ERROR, "图片不能为为空！");
        // 校验图片大小
        long size = file.getSize();
        final long ONE_M = 1024 * 1024L;
        // 最大限制为 6M
        ThrowUtils.throwIf(size > ONE_M * 6, ErrorCode.PARAMS_ERROR, "图片大小不能超过6M！");
        // 校验图片格式
        String suffix = FileUtil.getSuffix(file.getOriginalFilename());
        final List<String> ALLOW_FORMAT_LIST = List.of("jpg", "jpeg", "png", "webp");
        ThrowUtils.throwIf(!ALLOW_FORMAT_LIST.contains(suffix), ErrorCode.PARAMS_ERROR, "图片格式错误！");
    }

    /**
     * 删除临时文件
     *
     * @param file 文件
     */
    public void deleteTemporaryFile(File file) {
        if (file == null) {
            log.info("file is null");
            return;
        }
        // 删除临时文件
        boolean delete = file.delete();
        if (!delete) {
            log.error("file delete error, filepath = {}", file.getAbsolutePath());
        }
    }
}
