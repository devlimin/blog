package com.limin.blog.controller;

import com.limin.blog.model.Category;
import com.limin.blog.model.CategoryExample;
import com.limin.blog.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("man/list")
    public ModelAndView list(){
        ModelAndView mv = new ModelAndView("category/list");
        CategoryExample categoryExample = new CategoryExample();
        categoryExample.createCriteria().andUserIdEqualTo(1);
        List<Category> categories = categoryService.selectByExample(categoryExample);

        mv.addObject("categories", categories);
        return mv;
    }

    @GetMapping("man/delete")
    public void delete(@RequestParam(value = "id") Integer id) {
        categoryService.delete(id);
    }

    @PostMapping("man/updatename")
    public void updateName(Category category) {
        categoryService.updateName(category);
    }

    @PostMapping("man/add")
    public void add(Category category) {
        category.setUserId(1);
        categoryService.add(category);
    }
}
