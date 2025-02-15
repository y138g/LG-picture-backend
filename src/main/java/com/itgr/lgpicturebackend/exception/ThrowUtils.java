package com.itgr.lgpicturebackend.exception;

/**
 * @author y138g
 * 异常处理工具类（类似断言）
 */
public class ThrowUtils {

    /**
     * 条件成立则抛出异常
     *
     * @param condition 条件
     * @param e         异常
     */
    public static void throwIf(boolean condition, RuntimeException e) {
        if (condition) {
            throw e;
        }
    }

    /**
     * 条件成立则抛出异常
     *
     * @param condition 条件
     * @param errorCode 异常枚举
     */
    public static void throwIf(boolean condition, ErrorCode errorCode) {
        throwIf(condition, new BusinessException(errorCode));
    }

    /**
     * 条件成立则抛出异常
     *
     * @param condition 条件
     * @param errorCode 异常枚举
     * @param message   异常信息
     */
    public static void throwIf(boolean condition, ErrorCode errorCode, String message) {
        throwIf(condition, new BusinessException(errorCode, message));
    }
}
