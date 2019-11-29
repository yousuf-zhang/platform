package com.yousuf.platform.entity.system;

import com.yousuf.platform.common.jpa.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * <p> ClassName: Group
 * <p> Description: 组
 * 这里用于存放用户关系的
 * 比如公司 公司下面的各个部门
 *
 * @author zhangshuai 2019/11/25
 */
@Data
@Entity
@Table(name = "sys_group")
public class Group extends BaseEntity {
    private static final long serialVersionUID = 8883078738931874283L;
    @Column(name = "g_code")
    private String code;
    @Column(name = "g_name")
    private String name;
    @Column(name = "g_intro")
    private String intro;
}
