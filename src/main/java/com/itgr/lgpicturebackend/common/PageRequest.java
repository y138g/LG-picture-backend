package com.itgr.lgpicturebackend.common;

import lombok.Data;

/**
 * @author ：y138g
 * 分页请求包装类
 */
@Data
public class PageRequest {

    /**
     * 当前业号
     */
    private int page = 1;

    /**
     * 页面大小
     */
    private int size = 10;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序（默认降序）
     */
    private String sortOrder = "descend";
}
