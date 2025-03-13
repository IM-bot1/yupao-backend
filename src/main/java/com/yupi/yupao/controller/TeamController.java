package com.yupi.yupao.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.yupao.common.BaseResponse;
import com.yupi.yupao.common.ResultUtils;
import com.yupi.yupao.exception.BusinessException;
import com.yupi.yupao.model.domain.Team;
import com.yupi.yupao.model.domain.User;
import com.yupi.yupao.model.domain.dto.TeamQuery;
import com.yupi.yupao.model.domain.request.CreateTeamRequest;
import com.yupi.yupao.model.domain.request.JoinTeamRequest;
import com.yupi.yupao.model.domain.request.UpdateTeamRequest;
import com.yupi.yupao.model.domain.vo.TeamUserVO;
import com.yupi.yupao.service.TeamService;
import com.yupi.yupao.service.UserService;
import com.yupi.yupao.service.UserTeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import com.yupi.yupao.common.ErrorCode;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * ClassName:TeamControl
 * Package:com.yupi.yupao.controller
 * User:HP
 * Date:2025/3/12
 * Time:15:03

 * Author 周东汉
 * Version 1.0
 * Description:
 */
@RestController
@RequestMapping("/team")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true", allowedHeaders = "*")
@Slf4j
public class TeamController {
    @Resource
    private TeamService teamService;
    @Resource
    private UserService userService;
    @Resource
    private UserTeamService userTeamService;
    // 创建队伍
    @PostMapping("/create")
    public BaseResponse<Long> createTeam(@RequestBody CreateTeamRequest createTeamRequest, HttpServletRequest request) {
        if (createTeamRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getCurrentUser(request);
        Team team = new Team();
        BeanUtils.copyProperties(createTeamRequest, team);
        long teamId = teamService.createTeam(team, loginUser);
        return ResultUtils.success(teamId);
    }
    // 删除队伍
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody Long teamId) {
        if (teamId < 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean delete = teamService.removeById(teamId);
//        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
//        userTeamQueryWrapper.eq("user_id", teamId); // 这里的teamId是队伍的id
//        userTeamService.remove(userTeamQueryWrapper);
        if (!delete) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除失败");
        }
        return ResultUtils.success(true);
    }
    // 更新队伍
    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody UpdateTeamRequest updateTeamRequest, HttpServletRequest request) {
        if (updateTeamRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getCurrentUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        boolean update = teamService.updateTeam(updateTeamRequest,loginUser);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"更新失败");
        }
        return ResultUtils.success(true);
    }
    // 获取队伍信息
    @GetMapping("/get")
    public BaseResponse<Team> getTeam(@RequestParam("teamId") Long teamId) {
        if (teamId < 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return ResultUtils.success(team);
    }
    // 获取队伍列表
    @GetMapping("/list")
    public BaseResponse<List<TeamUserVO>> listTeams(@RequestBody TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean isAdmin = userService.isAdmin(request);
        List<TeamUserVO> teamList = teamService.listTeamUserVO(teamQuery,isAdmin);
        return ResultUtils.success(teamList);
    }
    // 获取队伍分页列表
    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> listTeamPage(@RequestBody TeamQuery teamQuery) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamQuery, team);
        Page<Team> page = new Page<>(teamQuery.getPageNum(), teamQuery.getPageSize());
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        Page<Team> pageResult = teamService.page(page, queryWrapper);
        if (pageResult == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return ResultUtils.success(pageResult);
    }
    // 加入队伍
    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody JoinTeamRequest joinTeamRequest, HttpServletRequest request) {
        if (joinTeamRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getCurrentUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        boolean result = teamService.joinTeam(joinTeamRequest, loginUser);
        return ResultUtils.success(result);
    }
}
