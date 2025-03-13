package com.yupi.yupao.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * ClassName:JoinTeamRequest
 * Package:com.yupi.yupao.model.domain.request
 * User:HP
 * Date:2025/3/13
 * Time:21:56
 * Author 周东汉
 * Version 1.0
 * Description:
 */
@Data
public class JoinTeamRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private long teamId;

    private String password;
}
