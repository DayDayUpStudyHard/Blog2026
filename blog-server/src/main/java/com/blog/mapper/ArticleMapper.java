package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.Article;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文章 Mapper，继承 MyBatis-Plus {@link BaseMapper}，自带 CRUD 方法。
 * 复杂查询（如多表联查）可在此接口添加方法并通过 XML 实现。
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
}
