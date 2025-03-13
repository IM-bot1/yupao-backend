package com.yupi.yupao.model.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.yupi.yupao.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * ClassName:TeamQuery
 * Package:com.yupi.yupao.model.domain.dto
 * User:HP
 * Date:2025/3/12
 * Time:16:27
 *
 * @Author 周东汉
 * @Version 1.0
 * Description:
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TeamQuery extends PageRequest {
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
     * 队伍状态 0 - 正常，1 - 私有，2 - 加密
     */
    private Integer status;

}
