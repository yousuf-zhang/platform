package com.yousuf.platform.entity.system;

import com.yousuf.platform.common.enums.TrueOrFalseEnum;
import com.yousuf.platform.common.enums.UserAccountCategoryEnum;
import com.yousuf.platform.common.jpa.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * <p> ClassName: UserAccount
 * <p> Description: 用户账号实体
 *
 * @author zhangshuai 2019/11/25
 */
@Data
@Entity
@Table(name = "sys_user_account")
public class UserAccount extends BaseEntity {
    private static final long serialVersionUID = -5132788493122240042L;
    @Column(name = "ua_user_id")
    private Long userId;
    @Column(name = "ua_category")
    private UserAccountCategoryEnum categoryEnum;
    @Column(name = "ua_open_code")
    private String openCode;
    @Column(name = "ua_is_delete")
    private TrueOrFalseEnum isDelete;
}
