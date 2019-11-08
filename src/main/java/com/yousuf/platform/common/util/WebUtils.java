package com.yousuf.platform.common.util;

import com.yousuf.platform.exception.UtilsException;
import com.yousuf.platform.exception.code.GlobalCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * ClassName: WebUtils
 * Description: web辅助类
 *
 * @author zhangshuai 2019/11/8
 */
public class WebUtils {
    /**IP头文件*/
    private static final String[] IP_HEADERS = {
            "X-Forwarded-For",
            "X-Real-IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP"
    };


    private WebUtils() {
        throw new UtilsException(GlobalCode.UTILS_ERROR, "WebUtils不允许实例化");
    }
    /**
     * Title: getRequest
     * Description: 获取request
     *
     * @return javax.servlet.http.HttpServletRequest
     *
     * @author zhangshuai 2019/11/8
     *
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return Objects.requireNonNull(attrs).getRequest();
    }

    /**
     * Title: getIpAddress
     * Description: 获取当前用户的登录IP
     *
     * @return java.lang.String
     *
     * @author zhangshuai 2019/11/8
     *
     */
    public static String getIpAddress() {
        for (String ipHeader : IP_HEADERS) {
            String ip = getRequest().getHeader(ipHeader);
            if (!StringUtils.isEmpty(ip) && !ip.contains("unknown")) {
                return ip;
            }
        }
        return getRequest().getRemoteAddr();
    }


}
