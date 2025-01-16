package com.itgr.lgpicturebackend.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ：y138g
 * 用户视图(脱敏)
 */
@Data
public class UserVO implements Serializable {

    private static final long serialVersionUID = -3171718299981738866L;

    /**
     * id
     */
    private Long id;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户编号
     */
    private String createNum;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色：user/gl_user/root
     */
    private String userRole;

}
