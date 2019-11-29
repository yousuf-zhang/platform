package com.yousuf.platform.entity.system;

import com.yousuf.platform.common.jpa.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * <p> ClassName: UserGroup
 * <p> Description: 用户组
 *
 * @author zhangshuai 2019/11/29
 */
@Data
@Entity
@Table(name = "sys_user_group")
public class UserGroup extends BaseEntity {
    private static final long serialVersionUID = -2907576641276696800L;
    @Column(name = "ug_user_id")
    private Long userId;
    @Column(name = "ug_group_id")
    private Long groupId;
}
