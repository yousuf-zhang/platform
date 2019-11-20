package com.yousuf.platform.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.common.base.Converter;
import com.yousuf.platform.common.core.BaseDTO;
import com.yousuf.platform.common.infrastructure.AuthToken;
import com.yousuf.platform.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * <p> Title: UserInfoDTO
 * <p> Description: 用户信息DTO
 *
 * @author yousuf zhang 2019/11/8
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDTO extends BaseDTO<UserInfoDTO, User> implements AuthToken {
    private static final long serialVersionUID = -1524846825994888401L;
    @NotNull(message = "用户名不能为空")
    private String username;
    @JSONField(serialize = false)
    @NotNull(message = "密码不能为空")
    private String password;
    private String likeName;
    private String loginIp;
    private Integer isFull;
    @JSONField(serialize = false)
    private Set<String> authorities;

    @Override
    protected Converter<UserInfoDTO, User> convert() {
        return null;
    }

    @Override
    public Integer isFull() {
        return this.isFull;
    }
    @Override
    public String getLikeName() {
        return this.likeName;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getUserId() {
        return String.valueOf(this.id);
    }

    @Override
    public String getLoginIp() {
        return this.loginIp;
    }


}
