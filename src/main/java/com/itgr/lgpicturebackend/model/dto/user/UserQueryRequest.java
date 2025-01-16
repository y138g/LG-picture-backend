package com.itgr.lgpicturebackend.model.dto.user;

import com.itgr.lgpicturebackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author ：y138g
 * 查询用户请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 177818604453372773L;

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
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色：user/gl_user/root
     */
    private String userRole;


}
