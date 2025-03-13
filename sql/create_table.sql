# 数据库初始化
# @author <a href="https://github.com/liyupi">程序员鱼皮</a>
# @from <a href="https://yupi.icu">编程导航知识星球</a>

-- 创建库
create database if not exists yupi;

-- 切换库
use yupi;

# 用户表
create table user
(
    username     varchar(256)                       null comment '用户名',
    id           bigint auto_increment comment 'id'
        primary key,
    gender       tinyint                            null comment '性别',
    userAccount  varchar(256)                       null comment '用户账号',
    avatarUrl    varchar(1024)                      null comment '用户头像',
    userPassword varchar(512)                       null comment '密码',
    email        varchar(512)                       null comment '邮箱',
    phone        varchar(128)                       null comment '手机号',
    userStatus   tinyint  default 0                 null comment '状态 0 - 正常，1 - 冻结',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除',
    userRole     int      default 0                 null comment '用户角色 0 - 普通用户，1 - 管理员',
    planetCode   varchar(256)                       null comment '星球代码',
    tags         varchar(1024)                      null comment '标签 json  列表'
)
    comment '用户表';


create table if not exists yupi.team
(
    id           bigint auto_increment comment '队伍id' primary key ,
    name     varchar(256)                       null comment '队伍名称',
    userId           bigint  comment '用户id',
    description    varchar(1024)                      null comment '描述',
    maxNum        int          default 1              null comment '队伍最大人数',
    password varchar(512)                       null comment '队伍密码',
    expireTime   datetime  null comment '队伍过期时间',
    status       int  default 0  not null comment '队伍状态 0 - 正常，1 - 私有，2 - 加密',
    createTime   datetime default CURRENT_TIMESTAMP null comment '队伍创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '队伍更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除'
)
    comment '队伍表';

create table if not exists yupi.user_team
(
    id           bigint auto_increment comment 'id' primary key ,
    userId           bigint  comment '用户id',
    teamId           bigint  comment '队伍id',
    joinTime     datetime  null comment '加入队伍时间',
    createTime   datetime default CURRENT_TIMESTAMP null comment '队伍创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '队伍更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除'
)
    comment '用户队伍关系表';

