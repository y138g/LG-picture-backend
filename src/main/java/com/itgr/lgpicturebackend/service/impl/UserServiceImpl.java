package com.itgr.lgpicturebackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itgr.lgpicturebackend.constant.UserConstant;
import com.itgr.lgpicturebackend.exception.BusinessException;
import com.itgr.lgpicturebackend.exception.ErrorCode;
import com.itgr.lgpicturebackend.exception.ThrowUtils;
import com.itgr.lgpicturebackend.mapper.UserMapper;
import com.itgr.lgpicturebackend.model.dto.user.UserAddRequest;
import com.itgr.lgpicturebackend.model.dto.user.UserLoginRequest;
import com.itgr.lgpicturebackend.model.dto.user.UserQueryRequest;
import com.itgr.lgpicturebackend.model.dto.user.UserRegisterRequest;
import com.itgr.lgpicturebackend.model.entity.User;
import com.itgr.lgpicturebackend.model.entity.enums.UserRoleEnum;
import com.itgr.lgpicturebackend.model.vo.LoginUserVO;
import com.itgr.lgpicturebackend.model.vo.UserVO;
import com.itgr.lgpicturebackend.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    // 默认密码
    private static final String DEFAULT_PASSWORD = "Ab@123456";

    @Override
    public long userRegister(UserRegisterRequest userRegisterRequest) {
        // 1. 校验参数异常
        ThrowUtils.throwIf(ObjUtil.isEmpty(userRegisterRequest),
                new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空"));

        // 2. 校验账号长度不小于四位，不大于32位
        String userAccount = userRegisterRequest.getUserAccount();
        ThrowUtils.throwIf(userAccount.length() < 4 || userAccount.length() > 32,
                new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度不小于4位或不能大于32位"));

        // 3. 校验两次密码是否相同
        String userPassword = userRegisterRequest.getUserPassword();

        ThrowUtils.throwIf(!userPassword.equals(userRegisterRequest.getCheckPassword()),
                new BusinessException(ErrorCode.PARAMS_ERROR, "两次密码不一致"));

        // 4. 校验密码长度不小于八位，不大于32位
        ThrowUtils.throwIf(userPassword.length() < 8 || userPassword.length() > 32,
                new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度不小于8位或不能大于32位"));

        // 5. 校验账号是否重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        ThrowUtils.throwIf(this.baseMapper.selectCount(queryWrapper) > 0,
                new BusinessException(ErrorCode.PARAMS_ERROR, "账号已存在"));

        // 6. 密码加密
        String encryptPassword = encryptPassword(userPassword);

        // 7. 用户编号生成
        int createNum = generateCreateNum();

        // 8. 插入数据
        User needUser = new User();
        needUser.setUserAccount(userRegisterRequest.getUserAccount());
        needUser.setUserPassword(encryptPassword);
        needUser.setUserName(userRegisterRequest.getUserName());
        needUser.setUserRole(UserRoleEnum.USER.getValue());
        needUser.setCreateNum(String.valueOf(createNum));
        ThrowUtils.throwIf(!this.save(needUser), new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败"));

        // 9. 返回新增用户 id
        return needUser.getId();
    }

    @Override
    public LoginUserVO loginUser(UserLoginRequest userLoginRequest, HttpServletRequest request) {
        // 为空校验放置 controller
        // 1. 校验账号是否合法
        String userAccount = userLoginRequest.getUserAccount();
        ThrowUtils.throwIf(userAccount.length() < 4 || userAccount.length() > 32,
                new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度不小于4位或不能大于32位"));

        // 2. 校验密码是否合法
        String userPassword = userLoginRequest.getUserPassword();
        ThrowUtils.throwIf(userPassword.length() < 8 || userPassword.length() > 32,
                new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度不小于8位或不能大于32位"));

        // 3. 校验账号是否存在\密码是否正确
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword(userPassword));
        User user = this.baseMapper.selectOne(queryWrapper);
        ThrowUtils.throwIf(ObjUtil.isEmpty(user), new BusinessException(ErrorCode.SYSTEM_ERROR, "系统异常"));

        LoginUserVO loginUserVO = getLoginUserVO(user);
        request.getSession().setAttribute(UserConstant.LOGIN_USER_KEY, loginUserVO);
        // 4. 返回用户信息
        return loginUserVO;
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        LoginUserVO loginUserVO = (LoginUserVO) request.getSession().getAttribute(UserConstant.LOGIN_USER_KEY);
        ThrowUtils.throwIf(ObjUtil.isEmpty(loginUserVO), new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户未登录"));
        User user = this.baseMapper.selectById(loginUserVO.getId());
        ThrowUtils.throwIf(ObjUtil.isEmpty(user), new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户未登录"));
        return user;
    }

    @Override
    public void logoutUser(HttpServletRequest request) {
        LoginUserVO loginUserVO = (LoginUserVO) request.getSession().getAttribute(UserConstant.LOGIN_USER_KEY);
        ThrowUtils.throwIf(ObjUtil.isEmpty(loginUserVO), new BusinessException(ErrorCode.OPERATION_ERROR, "用户未登录"));
        request.getSession().removeAttribute(UserConstant.LOGIN_USER_KEY);
    }

    @Override
    public String encryptPassword(String userPassword) {
        Digester md5 = new Digester(DigestAlgorithm.MD5);
        return md5.digestHex(SALT + userPassword);
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        // 脱敏用户信息
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtil.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public long addUser(UserAddRequest userAddRequest) {
        ThrowUtils.throwIf(ObjUtil.isEmpty(userAddRequest),
                new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空"));

        User user = new User();
        BeanUtil.copyProperties(userAddRequest, user);

        //设置角色
        String userRole = userAddRequest.getUserRole();
        ThrowUtils.throwIf(Objects.equals(userRole, UserConstant.ROOT_ROLE),
                new BusinessException(ErrorCode.OPERATION_ERROR, "不能给任何人分配 root 角色！！！"));
        ThrowUtils.throwIf(userRole == null, new BusinessException(ErrorCode.OPERATION_ERROR, "请选择用户角色"));
        user.setUserRole(userRole);

        // 设置默认密码：Ab@123456
        user.setUserPassword(encryptPassword(SALT + DEFAULT_PASSWORD));

        // 生成编号
        int createNum = generateCreateNum();
        user.setCreateNum(String.valueOf(createNum));

        ThrowUtils.throwIf(!this.save(user), new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败"));

        return user.getId();
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        // 1. 校验参数
        ThrowUtils.throwIf(ObjUtil.isEmpty(userQueryRequest),
                new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空"));

        // 2. 构造查询条件
        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String createNum = userQueryRequest.getCreateNum();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != 0, "id", id);
        queryWrapper.eq(StrUtil.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.eq(StrUtil.isNotBlank(createNum), "createNum", createNum);
        queryWrapper.like(StrUtil.isNotBlank(userAccount), "userAccount", userAccount);
        queryWrapper.like(StrUtil.isNotBlank(userName), "userName", userName);
        queryWrapper.like(StrUtil.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);

        return queryWrapper;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    private int generateCreateNum() {
        QueryWrapper<User> selectForCreateNum = new QueryWrapper<>();
        selectForCreateNum
                .select("createNum")
                .orderByDesc("createTime")
                .last("limit 1");
        User one = this.getOne(selectForCreateNum);
        return ObjUtil.isEmpty(one) ? 1 : Integer.parseInt(one.getCreateNum()) + 1;
    }
}




