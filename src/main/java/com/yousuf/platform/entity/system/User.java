package com.yousuf.platform.entity.system;

import com.yousuf.platform.common.enums.StatusEnum;
import com.yousuf.platform.common.enums.TrueOrFalseEnum;
import com.yousuf.platform.common.jpa.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

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
    @Column(name = "u_like_name")
    private String likeName;
    @Column(name = "u_password")
    private String password;
    @Column(name = "u_password_salt")
    private String salt;
    @Column(name = "u_status")
    private StatusEnum statusEnum;
    @Column(name = "u_is_delete")
    private TrueOrFalseEnum isDelete;
    @Column(name = "u_password_error")
    private Integer errorCount;
    @Column(name = "u_phone")
    private String phone;
    @Column(name = "u_email")
    private String email;
    @Column(name = "u_is_full")
    private TrueOrFalseEnum isFull;
    @Column(name = "u_is_lock")
    private TrueOrFalseEnum isLock;
    @Column(name = "u_unlock_time")
    private LocalDateTime unlockTime;
}
