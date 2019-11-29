package com.yousuf.platform.common.enums.convert;

import com.yousuf.platform.common.core.BaseEnum;
import com.yousuf.platform.common.enums.UserAccountCategoryEnum;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Objects;

/**
 * <p> Title: TrueOrFalseConvert
 * <p> Description: 枚举转换
 *
 * @author yousuf zhang 2019/11/17
 */
@Converter(autoApply = true)
public class UserAccountCategoryConvert implements AttributeConverter<UserAccountCategoryEnum, Integer> {
    @Override
    public Integer convertToDatabaseColumn(UserAccountCategoryEnum attribute) {
        return Objects.isNull(attribute) ? null : attribute.getCode();
    }

    @Override
    public UserAccountCategoryEnum convertToEntityAttribute(Integer dbData) {
        return Objects.isNull(dbData) ? null : BaseEnum.findByCode(UserAccountCategoryEnum.class, dbData);
    }
}
