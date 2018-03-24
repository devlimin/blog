package com.limin.blog.controller;

import com.limin.blog.enums.ArticleEnum;
import com.limin.blog.enums.CommentEnum;
import com.limin.blog.model.*;
import com.limin.blog.service.ArticleCategoryService;
import com.limin.blog.service.ArticleService;
import com.limin.blog.service.CategoryService;
import com.limin.blog.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ArticleCategoryService articleCategoryService;

    @Autowired
    private CommentService commentService;

    /**
     * 查看文章
     * @param id 文章ID
     * @return
     */
    @GetMapping(value = "detail/{id}")
    public ModelAndView article(@PathVariable("id") Integer id){
        ModelAndView mv = new ModelAndView("article/detail");
        //文章
        Article article = articleService.selectById(id);

        //文章相关的个人分类
        List<Category> articleCategories = new ArrayList<Category>();
        ArticleCategoryExample articleCategoryExample = new ArticleCategoryExample();
        articleCategoryExample.createCriteria().andArticleIdEqualTo(id);
        for(ArticleCategory articleCategory : articleCategoryService.select(articleCategoryExample)) {
            Category category = categoryService.selectById(articleCategory.getCategoryId());
            articleCategories.add(category);
        }

        //文章评论
        List<Comment> comments = commentService.selectPageByEntityWithBlog(CommentEnum.PUBLISHED.getKey(),id,0,10);

        //所有个人分类
        CategoryExample categoryExample = new CategoryExample();
        categoryExample.createCriteria().andUserIdEqualTo(1);
        List<Category> categories = categoryService.selectByExample(categoryExample);

        mv.addObject("article",article);
        mv.addObject("articleCategories",articleCategories);
        mv.addObject("comments",comments);
        mv.addObject("categories",categories);
        return mv;
    }

    /**
     * 查找用户文章，按时间降序
     * @param id 用户ID
     * @return
     */
    @GetMapping(value = "list/{id}")
    public String articles(@PathVariable("id") Integer id){
        ModelAndView mv = new ModelAndView();
        ArticleExample articleExample = new ArticleExample();
        articleExample.setOrderByClause("releaseDate decs");
        List<Article> articles = articleService.selectByExampleWithBLOBS(articleExample);

        //所有个人分类
        CategoryExample categoryExample = new CategoryExample();
        categoryExample.createCriteria().andUserIdEqualTo(1);
        List<Category> categories = categoryService.selectByExample(categoryExample);

        mv.addObject("articles", articles);
        mv.addObject("categories",categories);
        return "article/articles";
    }


    //全部
    @GetMapping("man/list/all")
    public String listAll(){
        ArticleExample articleExample = new ArticleExample();
        articleExample.createCriteria().andUserIdEqualTo(1);
        List<Article> articles = articleService.selectPageByExample(articleExample, 1,10);
        return "/article/list";
    }

    //已发表
    @GetMapping("man/list/published")
    public String listPublished(){
        ArticleExample articleExample = new ArticleExample();
        articleExample.createCriteria().andUserIdEqualTo(1).andStatusEqualTo(ArticleEnum.PUBLISHED.getKey());
        List<Article> articles = articleService.selectPageByExample(articleExample, 1,10);
        return "/article/list";
    }

    //草稿
    @GetMapping("man/list/draft")
    public String listDraft(){
        ArticleExample articleExample = new ArticleExample();
        articleExample.createCriteria().andUserIdEqualTo(1).andStatusEqualTo(ArticleEnum.DRAFT.getKey());
        List<Article> articles = articleService.selectPageByExample(articleExample, 1,10);
        return "/article/list";
    }

    //垃圾箱
    @GetMapping("man/list/garbage")
    public String listGarbage(){
        ArticleExample articleExample = new ArticleExample();
        articleExample.createCriteria().andUserIdEqualTo(1).andStatusEqualTo(ArticleEnum.GARBAGE.getKey());
        List<Article> articles = articleService.selectPageByExample(articleExample, 1,10);
        return "/article/list";
    }

    @GetMapping("man/edit")
    public String edit() {
        return "/article/edit";
    }

    @PostMapping("man/publish")
    public String publish(Article article) {
        article.setUserId(1);
        articleService.publish(article);
        return "/";
    }
    @PostMapping("man/draft")
    public String draft(Article article) {
        articleService.draft(article);
        return "";
    }
}
