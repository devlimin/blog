package com.limin.blog.service;

import com.limin.blog.enums.CategoryEnum;
import com.limin.blog.mapper.CategoryMapper;
import com.limin.blog.model.Category;
import com.limin.blog.model.CategoryExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "category")
public class CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

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
    }

    @CacheEvict(key = "'category:'+#id.toString()")
    public void updateName(Integer id, String name) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        categoryMapper.updateByPrimaryKeySelective(category);
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
}
