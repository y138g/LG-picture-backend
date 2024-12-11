package com.itgr.lgpicturebackend.service;

import com.itgr.lgpicturebackend.model.dto.UserRegisterRequest;
import com.itgr.lgpicturebackend.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author ygking
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2024-12-11 23:23:20
 */
public interface UserService extends IService<User> {
    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册请求
     * @return 新注册用户 id
     */
    long userRegister(UserRegisterRequest userRegisterRequest);

    /**
     * 密码加密
     *
     * @param userPassword 用户密码
     * @return 加密后的密码
     */
    String encryptPassword(String userPassword);
}
