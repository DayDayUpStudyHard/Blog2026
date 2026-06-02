package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.entity.ArticleLike;
import com.blog.mapper.ArticleLikeMapper;
import com.blog.service.ArticleLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 文章点赞服务实现。
 * <p>
 * Redis 结构：
 * <ul>
 *   <li>{@code article:likes:{id}} — Set，存储已点赞的 IP</li>
 *   <li>{@code article:like:count:{id}} — String，点赞计数</li>
 * </ul>
 * 每次操作同时写入 Redis 和 MySQL，Redis 为主读取源。
 */
@Service
@RequiredArgsConstructor
public class ArticleLikeServiceImpl implements ArticleLikeService {

    private static final String LIKES_SET_PREFIX = "article:likes:";
    private static final String COUNT_KEY_PREFIX = "article:like:count:";

    private final ArticleLikeMapper articleLikeMapper;
    private final StringRedisTemplate redisTemplate;

    @Override
    public Map<String, Object> toggle(Long articleId, String ip) {
        String setKey = LIKES_SET_PREFIX + articleId;
        String countKey = COUNT_KEY_PREFIX + articleId;
        Boolean isMember = redisTemplate.opsForSet().isMember(setKey, ip);

        if (Boolean.TRUE.equals(isMember)) {
            // 取消点赞
            redisTemplate.opsForSet().remove(setKey, ip);
            redisTemplate.opsForValue().decrement(countKey);
            articleLikeMapper.delete(new LambdaQueryWrapper<ArticleLike>()
                    .eq(ArticleLike::getArticleId, articleId)
                    .eq(ArticleLike::getUserIp, ip));
            long count = getCountFromRedis(articleId);
            return Map.of("liked", false, "count", count);
        } else {
            // 点赞
            redisTemplate.opsForSet().add(setKey, ip);
            Long newCount = redisTemplate.opsForValue().increment(countKey);
            ArticleLike like = new ArticleLike();
            like.setArticleId(articleId);
            like.setUserIp(ip);
            articleLikeMapper.insert(like);
            return Map.of("liked", true, "count", newCount != null ? newCount.longValue() : 0L);
        }
    }

    @Override
    public Map<String, Object> getLikeInfo(Long articleId, String ip) {
        String setKey = LIKES_SET_PREFIX + articleId;
        Boolean liked = redisTemplate.opsForSet().isMember(setKey, ip);
        long count = getCountFromRedis(articleId);
        return Map.of("liked", Boolean.TRUE.equals(liked), "count", count);
    }

    @Override
    public long getCount(Long articleId) {
        return getCountFromRedis(articleId);
    }

    /**
     * 从 Redis 读取点赞数，若 key 不存在则从 MySQL 补偿加载。
     */
    private long getCountFromRedis(Long articleId) {
        String countKey = COUNT_KEY_PREFIX + articleId;
        String cached = redisTemplate.opsForValue().get(countKey);
        if (cached != null) {
            return Long.parseLong(cached);
        }
        // 缓存未命中 → 从 MySQL 加载并回填 Redis
        long dbCount = articleLikeMapper.selectCount(
                new LambdaQueryWrapper<ArticleLike>().eq(ArticleLike::getArticleId, articleId));
        redisTemplate.opsForValue().set(countKey, String.valueOf(dbCount));
        return dbCount;
    }
}
