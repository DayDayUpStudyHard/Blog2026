package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 标签实体，映射 {@code t_tag} 表。
 */
@Data
@TableName("t_tag")
public class Tag {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
