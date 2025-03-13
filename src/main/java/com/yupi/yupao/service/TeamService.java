package com.yupi.yupao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.yupao.model.domain.Team;
import com.yupi.yupao.model.domain.User;
import com.yupi.yupao.model.domain.dto.TeamQuery;
import com.yupi.yupao.model.domain.request.JoinTeamRequest;
import com.yupi.yupao.model.domain.request.UpdateTeamRequest;
import com.yupi.yupao.model.domain.vo.TeamUserVO;

import java.util.List;


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
    /**
     * 搜索队伍
     * @param teamQuery
     * @return
     */
    List<TeamUserVO> listTeamUserVO(TeamQuery teamQuery, boolean isAdmin);

    /**
     * 更新队伍信息
     * @param updateTeamRequest
     * @return
     */
    boolean updateTeam(UpdateTeamRequest updateTeamRequest, User LoginUser);

    boolean joinTeam(JoinTeamRequest joinTeamRequest, User loginUser);
}
