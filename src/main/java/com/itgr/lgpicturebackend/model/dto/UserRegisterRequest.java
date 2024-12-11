package com.itgr.lgpicturebackend.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ：y138g
 *
 * 用户注册请求类
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 382910696671558709L;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 确认密码
     */
    private String checkPassword;

    /**
     * 用户昵称
     */
    private String userName;

}
