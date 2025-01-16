package com.itgr.lgpicturebackend.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.itgr.lgpicturebackend.exception.BusinessException;
import com.itgr.lgpicturebackend.exception.ErrorCode;
import com.itgr.lgpicturebackend.manager.CosManager;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.utils.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * @author y138g
 * 文件操作工具类（已作废）
 */
@Slf4j
@Deprecated
public class FileUtils {

    /**
     * 文件上传
     *
     * @param multipartFile 文件
     * @param userAccount   用户账号
     * @param cosManager    cos 客户端管理
     * @return 可访问地址
     */
    public static String upload(MultipartFile multipartFile, String userAccount,
                                CosManager cosManager) {
        // 文件目录
        String filename = multipartFile.getOriginalFilename();
        // 组成上传文件路径（uuid_时间戳_名称.后缀）
        String uuid = RandomUtil.randomString(16);
        String uploadName = String.format("%s_%s_%s", uuid, DateUtil.formatDate(new Date()), filename);
        String filepath = String.format("/%s/%s", userAccount, uploadName);
        File file = null;
        try {
            // 上传文件
            file = File.createTempFile(filepath, null);
            multipartFile.transferTo(file);
            cosManager.putObject(filepath, file);
            // 返回可访问地址
            return filepath;
        } catch (Exception e) {
            log.error("file upload error, filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            if (file != null) {
                // 删除临时文件
                boolean delete = file.delete();
                if (!delete) {
                    log.error("file delete error, filepath = {}", filepath);
                }
            }
        }
    }

    /**
     * 下载文件
     *
     * @param filePath   文件路径
     * @param cosManager cos 客户端管理
     */
    public static void download(String filePath, HttpServletResponse response, CosManager cosManager)
            throws IOException {
        COSObjectInputStream cosObjectInput = null;
        try {
            COSObject cosObject = cosManager.getObject(filePath);
            cosObjectInput = cosObject.getObjectContent();
            // 处理下载到的流
            byte[] bytes = IOUtils.toByteArray(cosObjectInput);
            // 设置响应头
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + filePath);
            // 写入响应
            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("file download error, filepath = " + filePath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载失败");
        } finally {
            if (cosObjectInput != null) {
                cosObjectInput.close();
            }
        }
    }
}
