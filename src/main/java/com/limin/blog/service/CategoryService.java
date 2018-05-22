package com.limin.blog.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.limin.blog.enums.CategoryEnum;
import com.limin.blog.exception.BizException;
import com.limin.blog.mapper.ArticleCategoryMapper;
import com.limin.blog.mapper.CategoryMapper;
import com.limin.blog.model.ArticleCategory;
import com.limin.blog.model.ArticleCategoryExample;
import com.limin.blog.model.Category;
import com.limin.blog.model.CategoryExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@CacheConfig(cacheNames = "category")
public class CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ArticleCategoryService articleCategoryService;

    public List<Category> selectByUserId(Integer userId){
        CategoryExample example = new CategoryExample();
        example.createCriteria().andUserIdEqualTo(userId);
        return categoryMapper.selectByExample(example);
    }

    public List<Category> selectByExample(CategoryExample categoryExample) {
        return categoryMapper.selectByExample(categoryExample);
    }

    @Cacheable(key = "'category:'+#id.toString()")
    public Category selectById(Integer id) {
        return categoryMapper.selectByPrimaryKey(id);
    }

    @CacheEvict(key = "'category:'+#id.toString()")
    public void delete(Integer id) {
        Category category = new Category();
        category.setId(id);
        category.setStatus(CategoryEnum.DELETED.getVal());
        categoryMapper.updateByPrimaryKeySelective(category);
        //删除关联关系
        ArticleCategoryExample example = new ArticleCategoryExample();
        example.createCriteria().andCategoryIdEqualTo(id);
        articleCategoryService.deleteByExample(example);
    }

    @CacheEvict(key = "'category:'+#id.toString()")
    public void updateName(Integer id, String name) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        categoryMapper.updateByPrimaryKeySelective(category);
    }

    @CacheEvict(key = "'category:'+#category.id.toString()")
    public Category update(Category category) {
        categoryMapper.updateByPrimaryKeySelective(category);
        return category;
    }

    public Integer add(Integer userId, String categoryName) {
        Category category = new Category();
        category.setUserId(userId);
        category.setName(categoryName);
        category.setArticleNum(0);
        category.setStatus(CategoryEnum.PUBLISHED.getVal());
        categoryMapper.insert(category);
        return category.getId();
    }

    public List<Category> selectByArticleId(Integer id) {
        CategoryExample example = new CategoryExample();
        ArticleCategoryExample articleCategoryExample =new ArticleCategoryExample();
        articleCategoryExample.createCriteria().andArticleIdEqualTo(id);
        List<ArticleCategory> articleCategories = articleCategoryService.select(articleCategoryExample);
        List<Category> categories = new ArrayList<>();
        if (articleCategories!=null) {
            for (ArticleCategory articleCategory : articleCategories) {
                Category category = selectById(articleCategory.getCategoryId());
                categories.add(category);
            }
        }
        return categories;
    }

    public PageInfo selectByExample(CategoryExample example, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Category> categories = categoryMapper.selectByExample(example);
        return new PageInfo(categories);
    }

    public void updateStatus(Integer categoryId, Integer status) {
        Category category = selectById(categoryId);
        if (category==null) {
            throw new BizException(2,"不存在该个人分类");
        }
        category.setStatus(status);
        categoryMapper.updateByPrimaryKeySelective(category);
    }
}
