package com.yousuf.platform.common.core;

import com.google.common.base.Converter;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p> Title: BaseDTO
 * <p> Description: DTO基类
 *
 * @author yousuf zhang 2019/11/19
 */
@Data
public abstract class BaseDTO<T, R> implements Serializable {
    private static final long serialVersionUID = -872995907734342197L;
    protected Long id;
    protected String createBy;
    protected LocalDateTime createAt;
    protected String modifiedBy;
    protected LocalDateTime modifiedAt;
    /**
     * <p>Title: convert
     * <p>Description: 谷歌转换代理
     *
     * @return com.google.common.base.Converter
     *
     * @author yousuf zhang 19-7-3
     **/
    protected abstract Converter<T, R> convert();

    public R convertFor (T t) {
        return convert().convert(t);
    }

    public T convertBack(R r) {
        return convert().reverse().convert(r);
    }
}
