package com.blog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.dto.ArticleDto;
import com.blog.entity.Article;

import java.util.Map;

public interface ArticleService {
    Page<Article> getPublishedList(int page, int size, Long categoryId, String keyword);
    Article getDetail(Long id);
    Map<String, Object> getNav(Long id);
    Article getById(Long id);
    Page<Article> getAdminList(int page, int size, Integer status);
    Article create(ArticleDto dto);
    Article update(Long id, ArticleDto dto);
    void delete(Long id);
}
