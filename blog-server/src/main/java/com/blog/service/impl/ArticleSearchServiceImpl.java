package com.blog.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.document.ArticleDocument;
import com.blog.entity.Article;
import com.blog.mapper.ArticleMapper;
import com.blog.repository.ArticleSearchRepository;
import com.blog.service.ArticleSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ES 文章搜索服务实现。
 * <p>
 * 通过 {@code blog.search.type=elasticsearch} 条件启用。
 * 索引时写入正文 + visibility，供 chat-assistant (Python) 做 RAG 检索。
 * 公共搜索只返回 visibility=PUBLIC 的文章。
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "blog.search.type", havingValue = "elasticsearch", matchIfMissing = false)
public class ArticleSearchServiceImpl implements ArticleSearchService {

    private final ArticleSearchRepository searchRepository;
    private final ArticleMapper articleMapper;

    @Override
    public Page<Article> search(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        org.springframework.data.domain.Page<ArticleDocument> esPage =
                searchRepository.findByTitleContainingOrSummaryContainingOrContentContaining(
                        keyword, keyword, keyword, pageable);

        List<Article> articles = esPage.getContent().stream()
                .map(doc -> articleMapper.selectById(doc.getId()))
                .filter(a -> a != null && "PUBLIC".equals(a.getVisibility()))
                .toList();

        Page<Article> result = new Page<>(page, size);
        result.setRecords(articles);
        result.setTotal(esPage.getTotalElements());
        return result;
    }

    @Override
    public void index(Article article) {
        try {
            ArticleDocument doc = ArticleDocument.builder()
                    .id(article.getId())
                    .title(article.getTitle())
                    .content(article.getContent())
                    .summary(article.getSummary())
                    .categoryId(article.getCategoryId())
                    .status(article.getStatus())
                    .visibility(article.getVisibility())
                    .createTime(article.getCreateTime())
                    .build();
            searchRepository.save(doc);
            log.debug("ES indexed article: {}", article.getId());
        } catch (Exception e) {
            log.warn("ES index failed for article {}: {}", article.getId(), e.getMessage());
        }
    }

    @Override
    public void delete(Long articleId) {
        try {
            searchRepository.deleteById(articleId);
            log.debug("ES deleted article: {}", articleId);
        } catch (Exception e) {
            log.warn("ES delete failed for article {}: {}", articleId, e.getMessage());
        }
    }
}
