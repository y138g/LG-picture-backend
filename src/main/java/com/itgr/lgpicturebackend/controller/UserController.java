package com.itgr.lgpicturebackend.controller;

import cn.hutool.core.util.ObjUtil;
import com.itgr.lgpicturebackend.common.BaseResponse;
import com.itgr.lgpicturebackend.common.ResultUtils;
import com.itgr.lgpicturebackend.exception.BusinessException;
import com.itgr.lgpicturebackend.exception.ErrorCode;
import com.itgr.lgpicturebackend.exception.ThrowUtils;
import com.itgr.lgpicturebackend.model.dto.UserRegisterRequest;
import com.itgr.lgpicturebackend.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author ：y138g
 *
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
}
