package com.itgr.lgpicturebackend.service.impl;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itgr.lgpicturebackend.exception.BusinessException;
import com.itgr.lgpicturebackend.exception.ErrorCode;
import com.itgr.lgpicturebackend.exception.ThrowUtils;
import com.itgr.lgpicturebackend.mapper.UserMapper;
import com.itgr.lgpicturebackend.model.dto.UserRegisterRequest;
import com.itgr.lgpicturebackend.model.entity.User;
import com.itgr.lgpicturebackend.model.entity.enums.UserRoleEnum;
import com.itgr.lgpicturebackend.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author ygking
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2024-12-11 23:23:20
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    // 盐值
    private static final String SALT = "kuangbaozhanlong";

    @Override
    public long userRegister(UserRegisterRequest userRegisterRequest) {
        // 1. 校验参数异常
        ThrowUtils.throwIf(ObjUtil.isEmpty(userRegisterRequest),
                new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空"));
        // 2. 校验账号长度不小于四位，不大于32位
        ThrowUtils.throwIf(userRegisterRequest.getUserAccount().length() < 4 ||
                        userRegisterRequest.getUserAccount().length() > 32,
                new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度不小于4位或不能大于32位"));
        // 3. 校验两次密码是否相同
        ThrowUtils.throwIf(!userRegisterRequest.getUserPassword().equals(userRegisterRequest.getCheckPassword()),
                new BusinessException(ErrorCode.PARAMS_ERROR, "两次密码不一致"));
        // 4. 校验账号是否重复
        String userAccount = userRegisterRequest.getUserAccount();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        ThrowUtils.throwIf(this.baseMapper.selectCount(queryWrapper) > 0,
                new BusinessException(ErrorCode.PARAMS_ERROR, "账号已存在"));
        // 5. 校验密码长度不小于八位，不大于32位
        ThrowUtils.throwIf(userRegisterRequest.getUserPassword().length() < 8 ||
                        userRegisterRequest.getUserPassword().length() > 32,
                new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度不小于8位或不能大于32位"));
        // 6. 密码加密
        String userPassword = userRegisterRequest.getUserPassword();
        String encryptPassword = encryptPassword(userPassword);
        // 7. 插入数据
        User needUser = new User();
        needUser.setUserAccount(userRegisterRequest.getUserAccount());
        needUser.setUserPassword(encryptPassword);
        needUser.setUserName(userRegisterRequest.getUserName());
        needUser.setUserRole(UserRoleEnum.USER.getValue());
        ThrowUtils.throwIf(!this.save(needUser), new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败"));
        return needUser.getId();
    }

    @Override
    public String encryptPassword(String userPassword) {
        Digester md5 = new Digester(DigestAlgorithm.MD5);
        return md5.digestHex(SALT + userPassword);
    }
}




