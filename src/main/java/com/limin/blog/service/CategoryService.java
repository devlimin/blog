package com.limin.blog.service;

import com.limin.blog.mapper.CategoryMapper;
import com.limin.blog.model.Category;
import com.limin.blog.model.CategoryExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    public Category selectById(Integer id) {
        return categoryMapper.selectByPrimaryKey(id);
    }

    public List<Category> selectByUserId(Integer userId){
        CategoryExample example = new CategoryExample();
        example.createCriteria().andUserIdEqualTo(userId);
        return categoryMapper.selectByExample(example);
    }

    public List<Category> selectByExample(CategoryExample categoryExample) {
        return categoryMapper.selectByExample(categoryExample);
    }

    public void delete(Integer id) {
        Category category = categoryMapper.selectByPrimaryKey(id);
        if(category == null) {

        }
        categoryMapper.deleteByPrimaryKey(id);
    }

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
        categoryMapper.insert(category);
        return category.getId();
    }
}
