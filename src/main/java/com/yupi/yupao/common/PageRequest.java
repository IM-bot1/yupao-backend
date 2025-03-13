package com.yupi.yupao.common;

import lombok.Data;

import java.io.Serializable;

/**
 * ClassName:PageRequest
 * Package:com.yupi.yupao.common
 * User:HP
 * Date:2025/3/12
 * Time:16:51
 *
 * @Author 周东汉
 * @Version 1.0
 * Description: PageRequest 类 用于分页查询
 */
@Data
public class PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 页面大小
     */
    protected int pageSize = 10;

    /**
     * 当前页号
     */
    protected int pageNum = 1;

}
