package com.yousuf.platform.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p> Title: DemoDTO
 * <p> Description: 测试DTO
 *
 * @author yousuf zhang 2019/11/5
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DemoDTO implements Serializable {
    private static final long serialVersionUID = -2719383277784473799L;
    @NotNull(message = "title不能为空")
    private String title;
    @Length(max = 10, message = "desc不能超过10个字符")
    private String desc;
}
