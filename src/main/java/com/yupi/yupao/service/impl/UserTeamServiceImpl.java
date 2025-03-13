package com.yupi.yupao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.yupi.yupao.mapper.UserTeamMapper;
import com.yupi.yupao.model.domain.UserTeam;
import com.yupi.yupao.service.UserTeamService;
import org.springframework.stereotype.Service;

/**
* @author HP
* @description 针对表【user_team(用户队伍关系表)】的数据库操作Service实现
* @createDate 2025-03-12 14:51:30
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService {

}




