package com.yousuf.platform.common.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * ClassName: PageData
 * Description: 分页信息
 *
 * @author zhangshuai 2019/8/26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageData<T> {
    private List<T> data;
    private Long total;

    /**
     * Title: of
     * Description: 获取分析信息
     *
     * @param data 记录数
     * @param total 总数
     *
     * @return com.pay.admin.common.page.PageData<T>
     *
     * @author zhangshuai 2019/8/26
     *
     */
    public static <T> PageData<T> of(List<T> data, long total) {
        return new PageData<>(data, total);
    }
}
