package com.yousuf.platform.entity.system;

import com.yousuf.platform.common.enums.PermissionCategoryEnum;
import com.yousuf.platform.common.enums.RequestMethodEnum;
import com.yousuf.platform.common.enums.StatusEnum;
import com.yousuf.platform.common.enums.TrueOrFalseEnum;
import com.yousuf.platform.common.jpa.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * <p> ClassName: Permission
 * <p> Description: 资源表
 *
 * @author zhangshuai 2019/11/29
 */
@Data
@Entity
@Table(name = "sys_permission")
public class Permission extends BaseEntity {
    private static final long serialVersionUID = -2091595195372909652L;
    @Column(name = "p_parent_id")
    private Long parentId;
    @Column(name = "p_code")
    private String code;
    @Column(name = "p_name")
    private String name;
    @Column(name = "p_intro")
    private String intro;
    @Column(name = "p_category")
    private PermissionCategoryEnum categoryEnum;
    @Column(name = "p_request_method")
    private RequestMethodEnum requestMethodEnum;
    @Column(name = "p_uri")
    private String uri;
    @Column(name = "p_order")
    private Integer order;
    @Column(name = "p_is_show")
    private TrueOrFalseEnum isShow;
    @Column(name = "p_status")
    private StatusEnum statusEnum;
    @Column(name = "p_is_auth")
    private TrueOrFalseEnum isAuth;
}
