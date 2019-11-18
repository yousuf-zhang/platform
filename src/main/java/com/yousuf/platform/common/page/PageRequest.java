package com.yousuf.platform.common.page;

import lombok.Data;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Objects;

/**
 * ClassName: PageRequest
 * Description: 分页请求信息
 *
 * @author zhangshuai 2019/8/26
 */
@Data
public class PageRequest {
    private static final String ORDER = "desc";
    private int page = 1;
    private int size = 10;
    private int offset;

    private Sort sorted = Sort.unsorted();
    private String direction;
    private String sort;



    public static Pageable valueOf(PageRequest request) {
        request.initSort();
        return org.springframework.data.domain.PageRequest.of(request.getPage()-1, request.getSize(),
                request.getSorted());
    }
    /**
     * Title: getOrderBy
     * Description: 组装排序参数
     *
     * @return java.lang.String
     *
     * @author zhangshuai 2019/8/27
     *
     */
    public String getOrderBy() {
        if (Objects.nonNull(sort)) {
            return " order by " + sort +" "+ (Objects.isNull(direction) ? ORDER : direction.toLowerCase());
        }
        return "";
    }

    private void initSort() {
        if (sort == null) {
            this.sort = "modifyAt";
        }

        Sort.Direction direction = Sort.Direction
                .fromOptionalString(this.direction)
                .orElse(Sort.Direction.DESC);
        this.sorted = Sort.by(direction, sort.trim().split(" +"));
    }

    public int getPage() {
        return this.page - 1;
    }

    public int getOffset() {
        return this.getPage() * this.getSize();
    }
}
