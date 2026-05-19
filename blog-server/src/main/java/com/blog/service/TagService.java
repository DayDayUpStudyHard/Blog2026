package com.blog.service;

import com.blog.entity.Tag;
import java.util.List;

public interface TagService {
    List<Tag> list();
    Tag create(Tag tag);
    void delete(Long id);
    List<Tag> getByArticleId(Long articleId);
}
