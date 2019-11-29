package com.yousuf.platform.entity.system;

import com.yousuf.platform.common.jpa.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * <p> ClassName: UserGroup
 * <p> Description: 角色组
 *
 * @author zhangshuai 2019/11/29
 */
@Data
@Entity
@Table(name = "sys_role_group")
public class RoleGroup extends BaseEntity {
    private static final long serialVersionUID = -2549662727951028054L;
    @Column(name = "rg_role_id")
    private Long roleId;
    @Column(name = "rg_group_id")
    private Long groupId;
}
