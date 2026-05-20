package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.entity.ArticleTag;
import com.blog.entity.Tag;
import com.blog.mapper.ArticleTagMapper;
import com.blog.mapper.TagMapper;
import com.blog.service.TagService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagServiceImpl implements TagService {

    private final TagMapper tagMapper;
    private final ArticleTagMapper articleTagMapper;

    public TagServiceImpl(TagMapper tagMapper, ArticleTagMapper articleTagMapper) {
        this.tagMapper = tagMapper;
        this.articleTagMapper = articleTagMapper;
    }

    @Override
    public List<Tag> list() {
        return tagMapper.selectList(null);
    }

    @Override
    public Tag create(Tag tag) {
        tag.setCreateTime(LocalDateTime.now());
        tagMapper.insert(tag);
        return tag;
    }

    @Override
    public Tag update(Tag tag) {
        tagMapper.updateById(tag);
        return tag;
    }

    @Override
    public void delete(Long id) {
        articleTagMapper.delete(new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getTagId, id));
        tagMapper.deleteById(id);
    }

    @Override
    public List<Tag> getByArticleId(Long articleId) {
        List<Long> tagIds = articleTagMapper.selectList(
                new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getArticleId, articleId))
                .stream().map(ArticleTag::getTagId).collect(Collectors.toList());
        if (tagIds.isEmpty()) return List.of();
        return tagMapper.selectBatchIds(tagIds);
    }
}
