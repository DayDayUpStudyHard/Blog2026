package com.blog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.dto.CommentDto;
import com.blog.entity.Comment;

import java.util.List;

/**
 * 评论服务接口。
 */
public interface CommentService {
    List<Comment> getByArticleId(Long articleId);
    Comment create(Long articleId, CommentDto dto);
    Page<Comment> getAdminList(int page, int size, Integer status, Integer type);
    void updateStatus(Long id, Integer status);
    void delete(Long id);
    Page<Comment> getGuestbookList(int page, int size);
    Comment createGuestbook(CommentDto dto);
}
