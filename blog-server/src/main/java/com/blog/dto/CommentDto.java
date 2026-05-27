package com.blog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 评论/留言 DTO。
 */
@Data
public class CommentDto {
    @NotBlank(message = "昵称不能为空")
    private String author;
    private String email;
    @NotBlank(message = "内容不能为空")
    private String content;
}
