package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.dto.CommentDto;
import com.blog.entity.Comment;
import com.blog.mapper.CommentMapper;
import com.blog.service.CommentService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

/**
 * 评论服务实现。
 * <p>
 * 前台查询只返回 status=1 的评论；新评论默认 status=1（直接展示）。
 * 后台可修改 status 实现审核/标记垃圾。
 */
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;

    public CommentServiceImpl(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }

    @Override
    public Page<Comment> getByArticleId(Long articleId, int page, int size) {
        return commentMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getArticleId, articleId)
                        .eq(Comment::getStatus, 1)
                        .orderByDesc(Comment::getCreateTime));
    }

    @Override
    public Comment create(Long articleId, CommentDto dto) {
        Comment comment = new Comment();
        comment.setArticleId(articleId);
        comment.setAuthor(dto.getAuthor());
        comment.setEmail(dto.getEmail());
        comment.setContent(dto.getContent());
        comment.setStatus(1);
        comment.setCreateTime(LocalDateTime.now());
        commentMapper.insert(comment);
        return comment;
    }

    @Override
    public Page<Comment> getAdminList(int page, int size, Integer status) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<Comment>()
                .orderByDesc(Comment::getCreateTime);
        if (status != null) wrapper.eq(Comment::getStatus, status);
        return commentMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Comment comment = commentMapper.selectById(id);
        if (comment == null) throw new IllegalArgumentException("留言不存在");
        comment.setStatus(status);
        commentMapper.updateById(comment);
    }

    @Override
    public void delete(Long id) {
        commentMapper.deleteById(id);
    }
}
