package com.yousuf.platform.vo;

import com.yousuf.platform.common.infrastructure.AuthToken;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p> Title: UserInfoDTO
 * <p> Description: //TODO
 *
 * @author yousuf zhang 2019/11/8
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDTO implements AuthToken {
    private String userId;
    private String username;
    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getUserId() {
        return this.userId;
    }
}
