package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_article")
public class Article {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String content;
    private String summary;
    private Long categoryId;
    private String cover;
    private Integer isTop;
    private Integer status;
    private Integer viewCount;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
