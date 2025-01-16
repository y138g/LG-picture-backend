package com.itgr.lgpicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itgr.lgpicturebackend.model.dto.user.UserAddRequest;
import com.itgr.lgpicturebackend.model.dto.user.UserLoginRequest;
import com.itgr.lgpicturebackend.model.dto.user.UserQueryRequest;
import com.itgr.lgpicturebackend.model.dto.user.UserRegisterRequest;
import com.itgr.lgpicturebackend.model.entity.User;
import com.itgr.lgpicturebackend.model.vo.LoginUserVO;
import com.itgr.lgpicturebackend.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
     * 用户登陆，返回脱敏用户信息
     *
     * @param userLoginRequest 用户登录请求
     * @param request          请求
     * @return 脱敏后的用户信息
     */
    LoginUserVO loginUser(UserLoginRequest userLoginRequest, HttpServletRequest request);

    /**
     * 获取当前登录用户信息(未脱敏)
     *
     * @param request 请求
     * @return 当前登录用户信息
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 用户注销
     *
     * @param request 请求
     */
    void logoutUser(HttpServletRequest request);

    /**
     * 密码加密
     *
     * @param userPassword 用户密码
     * @return 加密后的密码
     */
    String encryptPassword(String userPassword);

    /**
     * 获取脱敏用户信息
     *
     * @param user 用户信息
     * @return 脱敏用户信息
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 添加用户(仅 root 用户)
     *
     * @param userAddRequest 用户添加请求
     * @return 添加用户 id
     */
    long addUser(UserAddRequest userAddRequest);

    /**
     * 获取查询条件
     *
     * @param userQueryRequest 用户查询请求
     * @return 查询条件
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 获取脱敏用户
     *
     * @param user 用户
     * @return 脱敏用户
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏用户列表
     *
     * @param userList 用户列表
     * @return 脱敏用户列表
     */
    List<UserVO> getUserVOList(List<User> userList);
}
