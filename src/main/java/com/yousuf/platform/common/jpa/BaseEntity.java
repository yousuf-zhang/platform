package com.yousuf.platform.common.jpa;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p> Title: BaseEntity
 * <p> Description: 基础实体类 所有实体都应该继承该类
 *
 * @author yousuf
 * @since 19-5-8 下午9:54
 */
@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1646695318257712435L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "create_by")
    private String createBy;

    @CreatedDate
    @Column(name = "gmt_create", updatable = false)
    private LocalDateTime createAt;

    @Column(name = "modified_by")
    private String modifiedBy;

    @LastModifiedDate
    @Column(name = "gmt_modified")
    private LocalDateTime modifiedAt;

    @Column(name = "remark")
    private String remark;

}
