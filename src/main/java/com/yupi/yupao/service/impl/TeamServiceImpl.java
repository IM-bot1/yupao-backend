package com.yupi.yupao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.yupi.yupao.common.ErrorCode;
import com.yupi.yupao.exception.BusinessException;
import com.yupi.yupao.mapper.TeamMapper;
import com.yupi.yupao.model.domain.Team;
import com.yupi.yupao.model.domain.User;
import com.yupi.yupao.model.domain.UserTeam;
import com.yupi.yupao.model.domain.dto.TeamQuery;
import com.yupi.yupao.model.domain.enums.TeamStatusEnum;
import com.yupi.yupao.model.domain.request.JoinTeamRequest;
import com.yupi.yupao.model.domain.request.UpdateTeamRequest;
import com.yupi.yupao.model.domain.vo.TeamUserVO;
import com.yupi.yupao.model.domain.vo.UserVO;
import com.yupi.yupao.service.TeamService;
import com.yupi.yupao.service.UserService;
import com.yupi.yupao.service.UserTeamService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
* @author HP
* description 针对表【team(队伍表)】的数据库操作Service实现
* createDate 2025-03-12 14:47:39
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService {
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private UserTeamService userTeamService;
    @Resource
    private UserService userService;
    /**
     *
     * @param team
     * @param LoginUser
     * @return
     * description: 新建队伍
     */
    @Override
    public long createTeam(Team team, User LoginUser) {
        // 1 请求参数校验
        if(team == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        // 2 判断是否登录
        if(LoginUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        final long userId = LoginUser.getId();
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
    /**
     *
     * @param teamQuery
     * @return
     * description: 查询队伍列表
     */
    @Override
    public List<TeamUserVO> listTeamUserVO(TeamQuery teamQuery, boolean isAdmin) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        // 1.查询条件
        if(teamQuery != null){
            Long teamId = teamQuery.getId();
            String teamName = teamQuery.getName();
            Long userId = teamQuery.getUserId();
            String description = teamQuery.getDescription();
            Integer maxNum = teamQuery.getMaxNum();
            Integer status = teamQuery.getStatus();
            String searchKey = teamQuery.getSearchKey();
            // 根据队伍id查询
            if(teamId != null && teamId > 0){
                queryWrapper.eq("id", teamId);
            }
            // 根据搜索关键字查询
            if(StringUtils.isNotBlank(searchKey)){
                queryWrapper.and(qw -> qw.like("name", searchKey).or().like("description", searchKey));
            }
            // 根据队伍名称查询
            if(StringUtils.isNotBlank(teamName)){
                queryWrapper.like("name", teamName);
            }
            // 根据创建者查询
            if(userId != null && userId > 0){
                queryWrapper.eq("userId", userId);
            }
            // 根据描述查询
            if(StringUtils.isNotBlank(description)){
                queryWrapper.like("description", description);
            }
            // 根据最大人数查询
            if(maxNum != null && maxNum > 0){
                queryWrapper.eq("maxNum", maxNum);
            }
            // 根据状态查询
            TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(status);
            if (teamStatusEnum == null){
                teamStatusEnum = TeamStatusEnum.PUBLIC;
            }
            if (!isAdmin && !teamStatusEnum.equals(TeamStatusEnum.PUBLIC)){
                throw new BusinessException(ErrorCode.NO_AUTH);
            }
            queryWrapper.eq("status", teamStatusEnum.getValue());
        }
        // 不展示过期队伍
        queryWrapper.and(wrapper -> wrapper.isNull("expireTime").or().gt("expireTime", new Date()));
        // 2.查询队伍列表
        List<Team> teamList = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(teamList)){
            return new ArrayList<>();
        }
        List<TeamUserVO> teamUserVOList = new ArrayList<>();
        // 3.查询队伍创建人信息
        for (Team team : teamList){
            Long teamId = team.getId();
            if (teamId == null){
                continue;
            }
            // 脱敏
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team, teamUserVO);
            long userId = team.getUserId();
            User user = userService.getById(userId);
            if (user != null){
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(user, userVO);
                teamUserVO.setCreator(userVO);
            }
            teamUserVOList.add(teamUserVO);
        }
        return teamUserVOList;
    }
    /**
     *
     * @param updateTeamRequest
     * @return
     */
    @Override
    public boolean updateTeam(UpdateTeamRequest updateTeamRequest, User LoginUser) {
        // 1.请求参数校验
        if(updateTeamRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数不能为空");
        }
        // 获得数据库中的队伍信息
        Long teamId = updateTeamRequest.getId();
        if(teamId == null || teamId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍id不存在");
        }
        Team oldTeam = this.getById(teamId);
        if(oldTeam == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"队伍不存在");
        }
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(updateTeamRequest.getStatus());
        if (LoginUser.getId().equals(oldTeam.getUserId()) && userService.isAdmin(LoginUser)){
            throw new BusinessException(ErrorCode.NO_AUTH,"没有权限修改该队伍信息");
        }
        // 判断status
        if(teamStatusEnum.getValue() != oldTeam.getStatus()){// 状态改变
            if(StringUtils.isBlank(updateTeamRequest.getPassword())
                    && updateTeamRequest.getStatus().equals(TeamStatusEnum.SECRET.getValue())){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码不能为空");
            }else if(StringUtils.isNotBlank(updateTeamRequest.getPassword())
                    && !updateTeamRequest.getStatus().equals(TeamStatusEnum.SECRET.getValue())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"不是加密队伍，密码不能设置");
            }
        }
        // 2.更新队伍信息
        Team updateTeam = new Team();
        BeanUtils.copyProperties(updateTeamRequest, updateTeam);
        // 判断是否有修改信息
        boolean isEqual = true;
        try{
            for(Field field : Team.class.getDeclaredFields()){
                field.setAccessible(true);
                Object newValue = field.get(updateTeam);
                Object oldValue = field.get(oldTeam);
                if(newValue != null && oldValue != null && !newValue.equals(oldValue)){
                    isEqual = false;
                    break;
                }
            }
        }catch (Exception e){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"属性比较失败");
        }
        if(!isEqual){
            return this.updateById(updateTeam);
        }else{
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"没有要更新的信息");
        }
    }

    @Override
    public boolean joinTeam(JoinTeamRequest joinTeamRequest, User loginUser) {
        long userId = loginUser.getId();
        long teamId = joinTeamRequest.getTeamId();
        String password = joinTeamRequest.getPassword();
        // 1.判断用户加入的队伍是否超过5个
        QueryWrapper<UserTeam> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userId", userId);
        long count = userTeamService.count(userQueryWrapper);
        if (count > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "最多只能加入5个队伍");
        }
        // 2.判断队伍是否存在
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        // 3.判断队伍状态
        if (team.getStatus() == TeamStatusEnum.SECRET.getValue()) {
            if (StringUtils.isBlank(password)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "加密队伍密码不能为空");
            }
            if (!team.getPassword().equals(password)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
            }
        }else if (team.getStatus() == TeamStatusEnum.PRIVATE.getValue()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "私密队伍不能加入");
        }else {
            // 公开队伍直接加入
            if (!team.getPassword().equals(password)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "公开队伍不需要密码");
            }
        }
        // 4.判断队伍是否过期
        if (team.getExpireTime() != null && team.getExpireTime().before(new Date())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已过期");
        }
        // 5.只能加入未满员的队伍
        long joinTeamNum = userTeamService.count(new QueryWrapper<UserTeam>().eq("teamId", teamId));
        if (joinTeamNum == team.getMaxNum()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数已满");
        }
        // 6.判断用户是否已经加入该队伍
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("userId", userId).eq("teamId", teamId);
        UserTeam userTeam = userTeamService.getOne(userTeamQueryWrapper);
        if (userTeam != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "已经加入该队伍");
        }
        // 7.插入用户和队伍的关联信息
        UserTeam userTeamEntity = new UserTeam();
        userTeamEntity.setUserId(userId);
        userTeamEntity.setTeamId(teamId);
        userTeamEntity.setJoinTime(new Date());
        boolean userTeamSave = userTeamService.save(userTeamEntity);
        if (!userTeamSave) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "队伍和用户关联信息保存失败");
        }
        return true;
    }
}




