package com.blog.service;

import java.util.Map;

/**
 * 文章点赞服务接口。
 * <p>
 * 基于 IP 去重的点赞/取消点赞，Redis 做高速计数与去重，MySQL 持久化。
 */
public interface ArticleLikeService {
    /**
     * 切换点赞状态：未点赞→点赞，已点赞→取消。
     *
     * @param articleId 文章 ID
     * @param ip        用户 IP
     * @return {@code {liked: boolean, count: long}}
     */
    Map<String, Object> toggle(Long articleId, String ip);

    /**
     * 获取文章点赞信息。
     *
     * @param articleId 文章 ID
     * @param ip        用户 IP（用于判断当前用户是否已点赞）
     * @return {@code {liked: boolean, count: long}}
     */
    Map<String, Object> getLikeInfo(Long articleId, String ip);

    /**
     * 获取文章点赞数（不含用户状态）。
     */
    long getCount(Long articleId);
}
