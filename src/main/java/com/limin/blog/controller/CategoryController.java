package com.limin.blog.controller;

import com.limin.blog.constant.BlogConst;
import com.limin.blog.enums.CategoryEnum;
import com.limin.blog.model.Category;
import com.limin.blog.model.CategoryExample;
import com.limin.blog.model.User;
import com.limin.blog.service.CategoryService;
import com.limin.blog.util.ResponseUtil;
import com.limin.blog.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("man/list")
    public ModelAndView list(HttpSession session){
        User user = (User) session.getAttribute(BlogConst.LOGIN_SESSION_KEY);
        CategoryExample categoryExample = new CategoryExample();
        categoryExample.createCriteria().andUserIdEqualTo(user.getId()).andStatusEqualTo(CategoryEnum.PUBLISHED.getVal());
        List<Category> categories = categoryService.selectByExample(categoryExample);
        ModelAndView mv = new ModelAndView("category/list");
        mv.addObject("categories", categories);
        return mv;
    }

    @PostMapping("man/delete")
    @ResponseBody
    public Response delete(@RequestParam(value = "id") Integer id) {
        categoryService.delete(id);
        return ResponseUtil.success();
    }

    @PostMapping("man/update")
    @ResponseBody
    public Response updateName(@RequestParam(value = "id") Integer id,
                               @RequestParam(value = "name") String name) {
        categoryService.updateName(id, name);
        return ResponseUtil.success();
    }

    @PostMapping("man/add")
    @ResponseBody
    public Response add(@RequestParam(value = "name") String name, HttpSession session) {
        User user = (User) session.getAttribute(BlogConst.LOGIN_SESSION_KEY);
        Integer id  = categoryService.add(user.getId(), name);
        return ResponseUtil.success(id);
    }
}
