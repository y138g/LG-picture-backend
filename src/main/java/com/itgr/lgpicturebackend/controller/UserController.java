package com.itgr.lgpicturebackend.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itgr.lgpicturebackend.annotaction.AuthCheck;
import com.itgr.lgpicturebackend.common.BaseResponse;
import com.itgr.lgpicturebackend.common.DeleteRequest;
import com.itgr.lgpicturebackend.common.ResultUtils;
import com.itgr.lgpicturebackend.constant.UserConstant;
import com.itgr.lgpicturebackend.exception.BusinessException;
import com.itgr.lgpicturebackend.exception.ErrorCode;
import com.itgr.lgpicturebackend.exception.ThrowUtils;
import com.itgr.lgpicturebackend.model.dto.user.UserAddRequest;
import com.itgr.lgpicturebackend.model.dto.user.UserLoginRequest;
import com.itgr.lgpicturebackend.model.dto.user.UserQueryRequest;
import com.itgr.lgpicturebackend.model.dto.user.UserRegisterRequest;
import com.itgr.lgpicturebackend.model.entity.User;
import com.itgr.lgpicturebackend.model.vo.LoginUserVO;
import com.itgr.lgpicturebackend.model.vo.UserVO;
import com.itgr.lgpicturebackend.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author ：y138g
 * <p>
 * 用户接口
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册请求
     * @return 结果
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        ThrowUtils.throwIf(ObjUtil.isEmpty(userRegisterRequest),
                new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空"));
        long id = userService.userRegister(userRegisterRequest);
        return ResultUtils.success(id);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest 用户登录请求
     * @return 脱敏用户信息
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest,
                                              HttpServletRequest request) {
        ThrowUtils.throwIf(ObjUtil.isEmpty(userLoginRequest),
                new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空"));
        LoginUserVO loginUserVO = userService.loginUser(userLoginRequest, request);
        return ResultUtils.success(loginUserVO);
    }

    /**
     * 获取当前登录用户信息
     *
     * @param request 请求
     * @return 当前登录用户信息
     */
    @PostMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        ThrowUtils.throwIf(ObjUtil.isEmpty(request), new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "未登录"));
        User user = userService.getLoginUser(request);
        LoginUserVO loginUserVO = userService.getLoginUserVO(user);
        return ResultUtils.success(loginUserVO);
    }

    /**
     * 用户注销
     *
     * @param request 请求
     * @return 结果
     */
    @PostMapping("/logout")
    public BaseResponse<Void> userLogout(HttpServletRequest request) {
        ThrowUtils.throwIf(ObjUtil.isEmpty(request), new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "未登录"));
        userService.logoutUser(request);
        return ResultUtils.success(null);
    }

    /**
     * 添加用户（仅 root 用户）
     *
     * @param userAddRequest 用户添加请求
     * @return 结果
     */
    @AuthCheck(mustRole = UserConstant.ROOT_ROLE)
    @PostMapping("/root/add/user")
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        ThrowUtils.throwIf(ObjUtil.isEmpty(userAddRequest),
                new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空"));
        long id = userService.addUser(userAddRequest);
        return ResultUtils.success(id);
    }

    /**
     * 删除用户（仅 root 用户）
     *
     * @param deleteRequest 删除请求
     * @return 结果
     */
    @AuthCheck(mustRole = UserConstant.ROOT_ROLE)
    @PostMapping("/root/delete/user")
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        ThrowUtils.throwIf(ObjUtil.isEmpty(deleteRequest), new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空"));
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", deleteRequest.getId());
        boolean remove = userService.remove(queryWrapper);
        return ResultUtils.success(remove);
    }

    /**
     * 根据 id 获取用户信息（查询单个用户，普通用户及以上）
     *
     * @param id 用户 id
     * @return 用户信息（脱敏）
     */
    @AuthCheck(mustRole = UserConstant.USER_ROLE + UserConstant.GL_USER_ROLE + UserConstant.ROOT_ROLE)
    @PostMapping("/get/user/vo")
    public BaseResponse<UserVO> getUserVOById(Long id) {
        User user = userService.getById(id);
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        return ResultUtils.success(userVO);
    }

    /**
     * 分页获取用户封装列表（仅管理员）
     *
     * @param userQueryRequest 查询请求参数
     */
    @AuthCheck(mustRole = UserConstant.ROOT_ROLE)
    @PostMapping("/root/list/page/vo")
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest) {
        ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long current = userQueryRequest.getPage();
        long pageSize = userQueryRequest.getSize();
        Page<User> userPage = userService.page(new Page<>(current, pageSize),
                userService.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVOPage = new Page<>(current, pageSize, userPage.getTotal());
        List<UserVO> userVOList = userService.getUserVOList(userPage.getRecords());
        userVOPage.setRecords(userVOList);
        return ResultUtils.success(userVOPage);
    }
}
