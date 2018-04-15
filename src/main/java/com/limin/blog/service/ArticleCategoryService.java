package com.limin.blog.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.limin.blog.mapper.ArticleCategoryMapper;
import com.limin.blog.model.ArticleCategory;
import com.limin.blog.model.ArticleCategoryExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleCategoryService {

    @Autowired
    private ArticleCategoryMapper articleCategoryMapper;

    public void add(ArticleCategory articleCategory) {
        articleCategoryMapper.insertSelective(articleCategory);
    }

    public List<ArticleCategory> select(ArticleCategoryExample articleCategoryExample) {
        return articleCategoryMapper.selectByExample(articleCategoryExample);
    }

    public void deleteByExample(ArticleCategoryExample articleCategoryExample) {
        articleCategoryMapper.deleteByExample(articleCategoryExample);
    }

    public PageInfo selectPageByExample(ArticleCategoryExample example, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<ArticleCategory> articleCategories = articleCategoryMapper.selectByExample(example);
        return new PageInfo(articleCategories);
    }
}
