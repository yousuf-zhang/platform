package com.yousuf.platform.entity.system;

import com.yousuf.platform.common.jpa.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * <p> ClassName: UserGroup
 * <p> Description: 组资资源
 *
 * @author zhangshuai 2019/11/29
 */
@Data
@Entity
@Table(name = "sys_role_permission")
public class RolePermission extends BaseEntity {
    private static final long serialVersionUID = 8640164394798872428L;
    @Column(name = "rg_role_id")
    private Long roleId;
    @Column(name = "rg_permission_id")
    private Long permissionId;
}
