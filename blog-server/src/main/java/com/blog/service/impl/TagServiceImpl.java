package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.entity.ArticleTag;
import com.blog.entity.Tag;
import com.blog.mapper.ArticleTagMapper;
import com.blog.mapper.TagMapper;
import com.blog.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 标签服务实现。
 * <p>
 * 删除标签时先删除 {@code t_article_tag} 关联数据，再删标签。
 * {@code getByArticleId} 通过关联表查出标签 ID 集合，再批量查询标签实体。
 */
@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagMapper tagMapper;
    private final ArticleTagMapper articleTagMapper;

    @Cacheable(value = "tags", key = "'all'")
    @Override
    public List<Tag> list() {
        return tagMapper.selectList(null);
    }

    @CacheEvict(value = "tags", allEntries = true)
    @Override
    public Tag create(Tag tag) {
        tag.setCreateTime(LocalDateTime.now());
        tagMapper.insert(tag);
        return tag;
    }

    @CacheEvict(value = "tags", allEntries = true)
    @Override
    public Tag update(Tag tag) {
        tagMapper.updateById(tag);
        return tag;
    }

    @CacheEvict(value = "tags", allEntries = true)
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
