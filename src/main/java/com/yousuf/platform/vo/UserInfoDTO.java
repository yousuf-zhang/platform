package com.yousuf.platform.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.yousuf.platform.common.infrastructure.AuthToken;
import lombok.*;

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
public class UserInfoDTO implements AuthToken {
    private static final long serialVersionUID = -1524846825994888401L;
    private String userId;
    @NotNull(message = "用户名不能为空")
    private String username;
    @NotNull(message = "密码不能为空")
    private String password;
    private String likeName;
    private String loginIp;
    private Integer isFull;
    @JSONField(serialize = false)
    private Set<String> authorities;

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
        return this.userId;
    }

    @Override
    public String getLoginIp() {
        return this.loginIp;
    }
}
