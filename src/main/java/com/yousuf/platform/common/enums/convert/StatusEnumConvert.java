package com.yousuf.platform.common.enums.convert;

import com.yousuf.platform.common.core.BaseEnum;
import com.yousuf.platform.common.enums.StatusEnum;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Objects;

/**
 * <p> ClassName: StatusEnumConvert
 * <p> Description: StatusEnum转换
 *
 * @author zhangshuai 2019/11/22
 */
@Converter(autoApply = true)
public class StatusEnumConvert implements AttributeConverter<StatusEnum, Integer> {
    @Override
    public Integer convertToDatabaseColumn(StatusEnum attribute) {
        return Objects.isNull(attribute) ? null : attribute.getCode();
    }

    @Override
    public StatusEnum convertToEntityAttribute(Integer dbData) {
        return Objects.isNull(dbData) ? null : BaseEnum.findByCode(StatusEnum.class, dbData);
    }
}
