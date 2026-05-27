package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 评论实体，映射 {@code t_comment} 表。
 * <p>
 * {@code status}: 0=待审 1=通过 2=垃圾，前台仅展示 status=1 的评论。
 */
@Data
@TableName("t_comment")
public class Comment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long articleId;
    private String author;
    private String email;
    private String content;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
