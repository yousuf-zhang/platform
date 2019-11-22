create table sys_user
(
    id               bigint                                  not null comment 'id 自动生成'
        primary key,
    u_username       varchar(100)   default ''                null comment '用户名， 用作日志操作',
    u_like_name      varchar(100)                             not null comment '登录显示名称',
    u_password       varchar(100)                            not null comment '密码',
    u_password_salt  varchar(12)                             not null comment '密码加盐值',
    u_status         tinyint(1)    default 1                 not null comment '用户状态 1：正常 0:停用',
    u_is_delete      tinyint(1)    default 0                 not null comment '逻辑删除 0: 未删除 1： 已删除',
    u_password_error tinyint(1)    default 0                 not null comment '密码错误次数',
    u_phone          varchar(25)   default ''                not null comment '电话',
    u_email          varchar(50)   default ''                not null comment '邮箱',
    u_is_full        tinyint(1)    default 0                 not null comment '是否拥有所有权限，超级管理员 0：否 1：是',
    u_is_lock        tinyint(1)    default 0                 not null comment '是否锁定 0：否 1：是',
    u_unlock_time    timestamp     default CURRENT_TIMESTAMP not null comment '解锁时间',
    create_by        varchar(100)   default ''                not null comment '创建人',
    create_at        timestamp     default CURRENT_TIMESTAMP not null comment '创建时间',
    modified_by      varchar(100)   default ''                not null comment '修改人',
    modified_at      timestamp     default CURRENT_TIMESTAMP not null comment '修改时间',
    remark           varchar(1000) default ''                not null comment '备注'
)
    comment '用户信息表';

-- 用户账号表
create table sys_user_account
(
    id           bigint                                  not null comment 'id 自增'
        primary key,
    ua_user_id   bigint                                  not null comment '用户id',
    ua_category  tinyint(1)                              not null comment '账号类别',
    ua_open_code varchar(255)  default ''                not null comment '登录账号',
    ua_is_delete tinyint(1)    default 0                 not null comment '逻辑删除 0: 未删除 1： 已删除',
    create_by    varchar(100)  default ''                not null comment '创建人',
    create_at    timestamp     default CURRENT_TIMESTAMP not null comment '创建时间',
    modified_by  varchar(100)  default ''                not null comment '修改人',
    modified_at  timestamp     default CURRENT_TIMESTAMP not null comment '修改时间',
    remark       varchar(1000) default ''                not null comment '备注',
    constraint sys_user_account_ua_open_code_uindex
        unique (ua_open_code)
);

create index sys_user_account_ua_user_id_index
    on sys_user_account (ua_user_id);

-- 权限表
create table sys_permission
(
    id           bigint                                  not null comment 'id 自增'
        primary key,
    p_parent_id  bigint        default 0                 not null comment '父节点 默认0为根节点',
    p_code       varchar(255)                            not null comment '权限代码',
    p_name       varchar(255)  default ''                not null comment '权限名称',
    p_intro      varchar(255)  default ''                not null comment '权限说明',
    p_category   tinyint(2)    default 0                 not null comment '权限类别 0：模块 1：按钮 2：资源',
    p_is_show    tinyint(1)    default 1                 not null comment '是否显示 0：否 1：是',
    p_uri        varchar(255)  default ''                not null comment '资源路径',
    p_is_status  tinyint(1)    default 1                 not null comment '权限状态 0：停用 1：启用',
    p_is_exclude tinyint(1)    default 0                 not null comment '是否排除校验权限 0：否 1：是',
    create_by    varchar(100)  default ''                not null comment '创建人',
    create_at    timestamp     default CURRENT_TIMESTAMP not null comment '创建时间',
    modified_by  varchar(100)  default ''                not null comment '修改人',
    modified_at  timestamp     default CURRENT_TIMESTAMP not null comment '修改时间',
    remark       varchar(1000) default ''                not null comment '备注',
    constraint sys_permission_p_code_uindex
        unique (p_code)
)
    comment '权限表';

create index sys_permission_p_parent_id_index
    on sys_permission (p_parent_id);

-- 角色表
create table sys_role
(
    id bigint not null comment 'id 自增',
    r_parent_id bigint default 0 not null comment '父ID默认为0跟节点',
    r_code varchar(255) not null comment '角色代码',
    r_name varchar(255) default '' not null comment '角色名称',
    r_intro varchar(255) default '' not null comment '角色介绍',
    create_by    varchar(100)  default ''                not null comment '创建人',
    create_at    timestamp     default CURRENT_TIMESTAMP not null comment '创建时间',
    modified_by  varchar(100)  default ''                not null comment '修改人',
    modified_at  timestamp     default CURRENT_TIMESTAMP not null comment '修改时间',
    remark       varchar(1000) default '',
    constraint sys_role_pk
        primary key (id)
)
    comment '角色表';

create unique index sys_role_r_code_uindex
    on sys_role (r_code);

create index sys_role_r_parent_id_index
    on sys_role (r_parent_id);

-- 用户角色表
create table sys_user_role
(
    id bigint not null comment 'id',
    ur_user_id bigint not null comment '用户ID',
    ur_role_id bigint not null comment '角色ID',
    create_by varchar(100) default '' not null comment '创建人',
    create_at timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    modified_by varchar(100) default '' not null comment '修改人',
    modified_at timestamp default CURRENT_TIMESTAMP not null comment '修改时间',
    remark varchar(1000) default '' not null comment '备注',
    constraint sys_user_role_pk
        primary key (id)
)
    comment '用户角色表';;

create index sys_user_role_ur_role_id_index
    on sys_user_role (ur_role_id);

create index sys_user_role_ur_user_id_index
    on sys_user_role (ur_user_id);

-- 角色权限表
create table sys_role_permission
(
    id bigint not null comment 'ID',
    rp_role_id bigint not null comment '角色ID',
    rp_permission_id bigint not null comment '权限ID',
    create_by varchar(100) default '' not null comment '创建人',
    create_at timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    modified_by varchar(100) default '' not null comment '修改人',
    modified_at timestamp default CURRENT_TIMESTAMP not null comment '修改时间',
    remark varchar(1000) default '' not null comment '备注',
    constraint sys_role_permission_pk
        primary key (id)
)
    comment '角色权限表';

create index sys_role_permission_rp_permission_id_index
    on sys_role_permission (rp_permission_id);

create index sys_role_permission_rp_role_id_index
    on sys_role_permission (rp_role_id);

-- 组
create table sys_group
(
    id bigint not null comment 'ID',
    g_code varchar(255) not null comment '组代码',
    g_name varchar(255) default '' not null comment '组名称',
    g_intro varchar(255) default '' not null comment '说明',
    create_by varchar(100) default '' not null comment '创建人',
    create_at timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    modified_by varchar(100) default '' not null comment '修改人',
    modified_at timestamp default CURRENT_TIMESTAMP not null comment '修改时间',
    remark varchar(1000) default '' not null comment '备注',
    constraint sys_group_pk
        primary key (id)
)
    comment '介绍';

create unique index sys_group_g_code_uindex
    on sys_group (g_code);

-- 用户组关联表
create table sys_user_group
(
    id bigint not null comment 'ID',
    ug_user_id bigint not null comment '用户ID',
    ug_group_id bigint not null comment '组ID',
    create_by varchar(100) default '' not null comment '创建人',
    create_at timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    modified_by varchar(100) default '' not null comment '修改人',
    modified_at timestamp default CURRENT_TIMESTAMP not null comment '修改时间',
    remark varchar(1000) default '' not null comment '备注',
    constraint sys_user_group_pk
        primary key (id)
)
    comment '用户组关联表';

create index sys_user_group_ug_group_id_index
    on sys_user_group (ug_group_id);

create index sys_user_group_ug_user_id_index
    on sys_user_group (ug_user_id);

