package com.itgr.lgpicturebackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ：y138g
 * 用户登陆请求类
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 5241721219899626443L;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;
}
