package com.yupi.yupao.model.domain.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * ClassName:CreateTeamRequest
 * Package:com.yupi.yupao.model.domain.request
 * User:HP
 * Date:2025/3/12
 * Time:21:26
 *
 * @Author 周东汉
 * @Version 1.0
 * Description:
 */
@Data
public class CreateTeamRequest implements Serializable {

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
     * 队伍密码
     */
    private String password;

    /**
     * 队伍过期时间
     */
    private Date expireTime;

    /**
     * 队伍状态 0 - 正常，1 - 私有，2 - 加密
     */
    private Integer status;

}
