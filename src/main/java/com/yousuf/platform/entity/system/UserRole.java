package com.yousuf.platform.entity.system;

import com.yousuf.platform.common.jpa.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * <p> ClassName: UserRole
 * <p> Description: 用户角色
 *
 * @author zhangshuai 2019/11/29
 */
@Data
@Entity
@Table(name = "sys_user_role")
public class UserRole extends BaseEntity {
    private static final long serialVersionUID = -707002364784334124L;
    @Column(name = "ur_user_id")
    private Long userId;
    @Column(name = "ur_role_id")
    private Long roleId;
}
