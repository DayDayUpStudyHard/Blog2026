package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 文章分类实体，映射 {@code t_category} 表。
 * <p>
 * {@code sort} 控制展示顺序，列表查询按 sort 升序排列。
 */
@Data
@TableName("t_category")
public class Category {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;
    private Integer sort;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
