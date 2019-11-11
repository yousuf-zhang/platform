package com.yousuf.platform.auth.service.impl;

import com.yousuf.platform.auth.service.PermissionService;
import com.yousuf.platform.common.enums.TrueOrFalseEnum;
import com.yousuf.platform.vo.UserInfoDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import javax.annotation.PostConstruct;
import java.util.Objects;

/**
 * <p> Title: PermissionServiceImpl
 * <p> Description: 权限校验
 *
 * @author yousuf zhang 2019/11/11
 */
@Service
public class PermissionServiceImpl implements PermissionService {
    private static AntPathMatcher antPathMatcher;

    @PostConstruct
    private void init() {
        antPathMatcher = new AntPathMatcher();
    }
    @Override
    public boolean hasPermission(String requestUri, UserInfoDTO userInfo) {
        if (Objects.equals(userInfo.getIsFull(), TrueOrFalseEnum.TRUE.getCode())) {
            return true;
        }
        if (CollectionUtils.isEmpty(userInfo.getAuthorities())) {
            return false;
        }
        return userInfo.getAuthorities().stream().anyMatch(url -> antPathMatcher.match(url, requestUri));
    }
}
