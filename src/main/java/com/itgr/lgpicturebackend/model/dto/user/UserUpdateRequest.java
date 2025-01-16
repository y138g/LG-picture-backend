package com.itgr.lgpicturebackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ：y138g
 * 修改用户
 */
@Data
public class UserUpdateRequest implements Serializable {

    private static final long serialVersionUID = -8401929246312879045L;

    /**
     * id
     */
    private String id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

}
