package com.itgr.lgpicturebackend.exception;

/**
 * @author y138g
 * �쳣�������ࣨ���ƶ��ԣ�
 */
public class ThrowUtils {

    /**
     * �����������׳��쳣
     *
     * @param condition ����
     * @param e         �쳣
     */
    public static void throwIf(boolean condition, RuntimeException e) {
        if (condition) {
            throw e;
        }
    }

    /**
     * �����������׳��쳣
     *
     * @param condition ����
     * @param errorCode �쳣ö��
     */
    public static void throwIf(boolean condition, ErrorCode errorCode) {
        throwIf(condition, new BusinessException(errorCode));
    }

    /**
     * �����������׳��쳣
     *
     * @param condition ����
     * @param errorCode �쳣ö��
     * @param message   �쳣��Ϣ
     */
    public static void throwIf(boolean condition, ErrorCode errorCode, String message) {
        throwIf(condition, new BusinessException(errorCode, message));
    }
}
