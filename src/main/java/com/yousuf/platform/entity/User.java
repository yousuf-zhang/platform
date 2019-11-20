package com.yousuf.platform.entity;

import com.yousuf.platform.common.enums.TrueOrFalseEnum;
import com.yousuf.platform.common.jpa.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * <p> Title: User
 * <p> Description: 用户实体
 *
 * @author yousuf zhang 2019/11/19
 */
@Data
@Entity
@Table(name = "sys_user")
public class User extends BaseEntity {
    private static final long serialVersionUID = 8382797661305731886L;
    @Column(name = "u_username")
    private String username;
    @Column(name = "u_password")
    private String password;
    @Column(name = "u_like_name")
    private String likeName;
    @Column(name = "u_login_ip")
    private String loginIp;
    @Column(name = "u_is_full")
    private TrueOrFalseEnum isFull;
    @Column(name = "u_is_lock")
    private TrueOrFalseEnum isLock;
}
