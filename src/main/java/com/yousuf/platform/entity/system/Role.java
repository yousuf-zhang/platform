package com.yousuf.platform.entity.system;

import com.yousuf.platform.common.jpa.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * <p> ClassName: Role
 * <p> Description: 角色实体
 *
 * @author zhangshuai 2019/11/29
 */
@Data
@Entity
@Table(name = "sys_role")
public class Role extends BaseEntity {
    private static final long serialVersionUID = -3110207688087978045L;
    @Column(name = "r_parent_id")
    private Long parentId;
    @Column(name = "r_code")
    private String code;
    @Column(name = "r_name")
    private String name;
    @Column(name = "r_intro")
    private String intro;
}
