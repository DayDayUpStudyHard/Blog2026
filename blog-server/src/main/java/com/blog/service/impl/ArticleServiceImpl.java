package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.dto.ArticleDto;
import com.blog.entity.Article;
import com.blog.entity.ArticleTag;
import com.blog.mapper.ArticleMapper;
import com.blog.mapper.ArticleTagMapper;
import com.blog.service.ArticleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class ArticleServiceImpl implements ArticleService {

    private final ArticleMapper articleMapper;
    private final ArticleTagMapper articleTagMapper;

    public ArticleServiceImpl(ArticleMapper articleMapper, ArticleTagMapper articleTagMapper) {
        this.articleMapper = articleMapper;
        this.articleTagMapper = articleTagMapper;
    }

    @Override
    public Page<Article> getPublishedList(int page, int size, Long categoryId, String keyword) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<Article>()
                .eq(Article::getStatus, 1)
                .orderByDesc(Article::getIsTop)
                .orderByDesc(Article::getCreateTime);
        if (categoryId != null) wrapper.eq(Article::getCategoryId, categoryId);
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(Article::getTitle, keyword).or().like(Article::getSummary, keyword));
        }
        Page<Article> result = articleMapper.selectPage(new Page<>(page, size), wrapper);
        result.getRecords().forEach(a -> a.setContent(null));
        return result;
    }

    @Override
    public Article getDetail(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null || article.getStatus() != 1) {
            throw new IllegalArgumentException("文章不存在");
        }
        article.setViewCount(article.getViewCount() + 1);
        articleMapper.updateById(article);
        return article;
    }

    @Override
    public Article getById(Long id) {
        return articleMapper.selectById(id);
    }

    @Override
    public Page<Article> getAdminList(int page, int size, Integer status) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<Article>()
                .orderByDesc(Article::getCreateTime);
        if (status != null) wrapper.eq(Article::getStatus, status);
        return articleMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    @Transactional
    public Article create(ArticleDto dto) {
        Article article = new Article();
        article.setTitle(dto.getTitle());
        article.setContent(dto.getContent());
        article.setSummary(dto.getSummary());
        article.setCover(dto.getCover());
        article.setCategoryId(dto.getCategoryId());
        article.setIsTop(dto.getIsTop() != null ? dto.getIsTop() : 0);
        article.setStatus(dto.getStatus() != null ? dto.getStatus() : 0);
        article.setViewCount(0);
        LocalDateTime now = LocalDateTime.now();
        article.setCreateTime(now);
        article.setUpdateTime(now);
        articleMapper.insert(article);

        if (dto.getTagIds() != null) {
            for (Long tagId : dto.getTagIds()) {
                ArticleTag at = new ArticleTag();
                at.setArticleId(article.getId());
                at.setTagId(tagId);
                articleTagMapper.insert(at);
            }
        }
        return article;
    }

    @Override
    @Transactional
    public Article update(Long id, ArticleDto dto) {
        Article article = articleMapper.selectById(id);
        if (article == null) throw new IllegalArgumentException("文章不存在");
        article.setTitle(dto.getTitle());
        article.setContent(dto.getContent());
        article.setSummary(dto.getSummary());
        article.setCover(dto.getCover());
        article.setCategoryId(dto.getCategoryId());
        if (dto.getIsTop() != null) article.setIsTop(dto.getIsTop());
        if (dto.getStatus() != null) article.setStatus(dto.getStatus());
        article.setUpdateTime(LocalDateTime.now());
        articleMapper.updateById(article);

        articleTagMapper.delete(new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getArticleId, id));
        if (dto.getTagIds() != null) {
            for (Long tagId : dto.getTagIds()) {
                ArticleTag at = new ArticleTag();
                at.setArticleId(id);
                at.setTagId(tagId);
                articleTagMapper.insert(at);
            }
        }
        return article;
    }

    @Override
    public void delete(Long id) {
        articleTagMapper.delete(new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getArticleId, id));
        articleMapper.deleteById(id);
    }
}
