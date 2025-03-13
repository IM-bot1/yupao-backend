package com.yupi.yupao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.yupao.model.domain.Team;
import com.yupi.yupao.model.domain.User;


/**
* @author HP
* @description 针对表【team(队伍表)】的数据库操作Service
* @createDate 2025-03-12 14:47:39
*/
public interface TeamService extends IService<Team> {

    /**
     * 创建队伍
      * @param team
     * @return
     */
    long createTeam(Team team, User LoginUser);
}
