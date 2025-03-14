package com.yupi.yupao.model.domain.vo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * ClassName:UserVO
 * Package:com.yupi.yupao.model.domain.vo
 * User:HP
 * Date:2025/3/13
 * Time:16:01
 * Author 周东汉
 * Version 1.0
 * Description:
 */
@TableName("user")
@Data
public class UserVO implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private String gender;
    /**
     * 简介
     */
    private String profile;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态 0 - 正常
     */
    private Integer userStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     *
     */
    private Date updateTime;


    /**
     * 用户角色 0 - 普通用户 1 - 管理员
     */
    private Integer userRole;

    /**
     * 星球编号
     */
    private String planetCode;

    /**
     * 标签列表 json
     */
    private String tags;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
