package com.yousuf.platform.common.util;

import com.yousuf.platform.exception.UtilsException;
import com.yousuf.platform.exception.code.GlobalCode;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * ClassName: DateUtils
 * <p> Description: 日期处理辅助类
 *
 * @author zhangshuai 2019/11/7
 */
public class DateUtils {
    private DateUtils () {
        throw new UtilsException(GlobalCode.UTILS_ERROR, "不允许实例化");
    }
    /**
     * <p> Title: localDateTimeToDateConverter
     * <p> Description: 时间转换
     *
     * @param localDateTime 时间
     *
     * @return java.util.Date
     * @throws
     *
     * @author zhangshuai 2019/11/7
     *
     */
    public static Date localDateTimeToDateConverter(LocalDateTime localDateTime) {
        ZoneId zoneId = ZoneId.systemDefault();
        return Date.from(localDateTime.atZone(zoneId).toInstant());
    }
}
