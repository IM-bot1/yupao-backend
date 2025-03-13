package com.yupi.yupao.model.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * ClassName:TeamUserVO
 * Package:com.yupi.yupao.model.domain.vo
 * User:HP
 * Date:2025/3/13
 * Time:15:58
 *
 * Author 周东汉
 * Version 1.0
 * Description:
 */
@TableName("team_user")
@Data
public class TeamUserVO implements Serializable {
    /**
     * 队伍id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 描述
     */
    private String description;

    /**
     * 队伍最大人数
     */
    private Integer maxNum;


    /**
     * 队伍过期时间
     */
    private Date expireTime;

    /**
     * 队伍状态 0 - 正常，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 队伍创建时间
     */
    private Date createTime;

    /**
     * 队伍更新时间
     */
    private Date updateTime;

    /**
     * 创建人信息
     */
    private UserVO creator;
}
