package com.yousuf.platform.common.page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * ClassName: QueryParams
 * Description: 查询参数
 *
 * @author zhangshuai 2019/8/27
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryParams {
    private String sql;
    private String countSql;
    private List<Object> paramList;
    private Map<String, Object> aliasParams;
    private Class<?> target;
    private Integer limit;
    private Integer offset;
    private String orderBy;

    /**
     * Title: of
     * Description: 组装分页参数
     *
     * @param sql sql
     * @param paramList 查询条件
     * @param target 返回类型
     * @param pageRequest 分页参数
     *
     * @return com.pay.admin.common.top.QueryParams
     * @throws
     *
     * @author zhangshuai 2019/8/27
     *
     */
    public static QueryParams of(String sql, List<Object> paramList, Class<?> target, PageRequest pageRequest) {
     return QueryParams.builder()
             .sql(sql)
             .paramList(paramList)
             .target(target)
             .offset(pageRequest.getOffset())
             .limit(pageRequest.getSize())
             .orderBy(pageRequest.getOrderBy())
             .build();
    }

    /**
     * Title: of
     * Description: 组装查询条件
     *
     * @param sql sql
     * @param aliasParams 别名参数
     * @param target 类型
     * @param pageRequest 分页参数
     *
     * @return com.pay.admin.common.top.QueryParams
     * @throws
     *
     * @author zhangshuai 2019/8/27
     *
     */
    public static QueryParams of(String sql, Map<String, Object> aliasParams, Class<?> target, PageRequest pageRequest) {
        return QueryParams.builder()
                .sql(sql)
                .aliasParams(aliasParams)
                .target(target)
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getSize())
                .orderBy(pageRequest.getOrderBy())
                .build();
    }
}
