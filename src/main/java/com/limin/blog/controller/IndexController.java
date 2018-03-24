package com.limin.blog.controller;

import com.limin.blog.mapper.SysCategoryMapper;
import com.limin.blog.model.SysCategory;
import com.limin.blog.model.SysCategoryExample;
import com.limin.blog.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/")
public class IndexController {

    @Autowired
    SysCategoryMapper sysCategoryMapper;

    @Autowired
    ArticleService articleService;


    @GetMapping({"/","index"})
    public ModelAndView index(){
        ModelAndView mv = new ModelAndView("index");
        SysCategoryExample sysCategoryExample = new SysCategoryExample();
        sysCategoryExample.setOrderByClause("id asc");
        List<SysCategory> sysCategories=  sysCategoryMapper.selectByExample(sysCategoryExample);
        mv.addObject("sysCategories", sysCategories);
        return mv;
    }
}
