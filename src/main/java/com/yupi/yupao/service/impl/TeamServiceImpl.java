package com.yupi.yupao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.yupi.yupao.common.ErrorCode;
import com.yupi.yupao.exception.BusinessException;
import com.yupi.yupao.mapper.TeamMapper;
import com.yupi.yupao.model.domain.Team;
import com.yupi.yupao.model.domain.User;
import com.yupi.yupao.model.domain.UserTeam;
import com.yupi.yupao.model.domain.enums.TeamStatusEnum;
import com.yupi.yupao.service.TeamService;
import com.yupi.yupao.service.UserTeamService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Date;
import java.util.Optional;

/**
* @author HP
* @description 针对表【team(队伍表)】的数据库操作Service实现
* @createDate 2025-03-12 14:47:39
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService {
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private UserTeamService userTeamService;
    @Override
    public long createTeam(Team team, User LoginUser) {

        final long userId = LoginUser.getId();
        // 1 请求参数校验
        if(team == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        // 2 判断是否登录
        if(LoginUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        // 3 校验信息
        //  1).队伍人数 > 1 and <= 20
//        System.out.println("team.getMaxNum() = " + team.getMaxNum());
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if(maxNum < 1 || maxNum > 20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数必须在1-20之间");
        }
        // 2).队伍标题 <= 20
        String name = team.getName();
        if(StringUtils.isBlank(name) || name.length() > 20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍标题不满足要求");
        }
        // 3).队伍描述 <= 512
        String description = team.getDescription();
        if(StringUtils.isNotBlank(description) && description.length() > 512){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述不能为空且不能超过512个字符");
        }
        // 4).status 是否公开（int）不传默认为0
        int status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(status);
        if (teamStatusEnum == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态不正确");
        }
        // 5).如果status 是加密，则需要密码 and <= 32
        String password = team.getPassword();
        if (TeamStatusEnum.SECRET == teamStatusEnum && (StringUtils.isBlank(password) || password.length() > 32)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "加密队伍密码不能为空且不能超过32个字符");
        }
        // 6).超时时间 > 当前时间
        Date expireTime = team.getExpireTime();
        if(expireTime != null && expireTime.before(new Date())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "超时时间不能小于当前时间");
        }
        // 7).最多只能创建5个队伍
        // todo 这里应该查询当前用户创建的队伍数量，然后判断是否超过5个
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        long hasTeamCount = this.count(queryWrapper);
        if(hasTeamCount >= 5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "最多只能创建5个队伍");
        }
        // 4.插入队伍信息 和 用户和队伍的关联信息
        team.setId(null);
        team.setUserId(userId);
        transactionTemplate.execute(transactionStatus -> {
            try {
                boolean teamSave = this.save(team);
                if (!teamSave){
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "队伍信息保存失败");
                }
                // 同时插入用户和队伍的关联信息
                UserTeam userTeam = new UserTeam();
                userTeam.setUserId(userId);
                userTeam.setTeamId(team.getId());
                userTeam.setJoinTime(new Date());
                boolean userTeamSave = userTeamService.save(userTeam);
                if (!userTeamSave){
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "队伍和用户关联信息保存失败");
                }
            }catch (Exception e){
                transactionStatus.setRollbackOnly();
                throw e;
            }
            return true;
        });
        return team.getId();
    }
}




