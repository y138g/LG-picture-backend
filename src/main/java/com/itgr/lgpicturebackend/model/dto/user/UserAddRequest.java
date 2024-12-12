package com.itgr.lgpicturebackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ：y138g
 * 新增用户（管理员）
 */
@Data
public class UserAddRequest implements Serializable {

    private static final long serialVersionUID = -1804389496493979031L;

    /**
     * 账号
     */
    private String userAccount;

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
