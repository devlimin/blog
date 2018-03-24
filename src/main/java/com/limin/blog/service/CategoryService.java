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

    public List<Category> selectByExample(CategoryExample categoryExample) {
        return categoryMapper.selectByExample(categoryExample);
    }

    public void add(Category category) {
        categoryMapper.insertSelective(category);
    }

    public void delete(Integer id) {
        if(categoryMapper.selectByPrimaryKey(id) == null) {

        }
        categoryMapper.deleteByPrimaryKey(id);
    }

    public void updateName(Category category) {
        if (categoryMapper.selectByPrimaryKey(category.getId()) == null) {

        }
        categoryMapper.updateByPrimaryKeySelective(category);
    }
}
