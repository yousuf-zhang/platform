package com.yousuf.platform.common.jpa.impl;

import com.yousuf.platform.common.util.UserContextHelper;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

/**
 * <p> Title: UserAuditorAware
 * <p> Description: 自动新增修改人,新增人
 *
 * @author yousuf zhang 2019/11/17
 */
@SuppressWarnings("NullableProblems")
@Component
public class UserAuditorAware implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        return Objects.isNull(UserContextHelper.getCurrentUser()) ?
        Optional.empty() : Optional.of(UserContextHelper.getCurrentUser().getUsername());
    }
}
