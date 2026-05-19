package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

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
