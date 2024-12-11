package com.itgr.lgpicturebackend.model.entity.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * @author: y138g
 *
 * 用户角色枚举类
 */
@Getter
public enum UserRoleEnum {

    USER("用户", "user"),
    GL_USER("信技用户", "gl_user"),
    ROOT("管理员", "root");

    private final String text;

    private final String value;

    UserRoleEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value 枚举值
     * @return 枚举
     */
    public static UserRoleEnum getEnumByValue(String value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (UserRoleEnum userRoleEnum : UserRoleEnum.values()) {
            if (value.equals(userRoleEnum.value)) {
                return userRoleEnum;
            }
        }
        return null;
    }
}
