package com.limin.blog.controller;

import com.github.pagehelper.PageInfo;
import com.limin.blog.enums.ArticleTypeEnum;
import com.limin.blog.enums.CommentEnum;
import com.limin.blog.model.*;
import com.limin.blog.service.*;
import com.limin.blog.util.ResponseUtil;
import com.limin.blog.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
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

    @Autowired
    private SysCategoryService sysCategoryService;

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
        List<Comment> comments = commentService.selectPageByEntityWithBlog(CommentEnum.PUBLISHED.getVal(),id,0,10);

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
        articleExample.setOrderByClause("releaseDate desc");
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
    @GetMapping("man/list")
    public String listAll(Integer pageNum, Integer pageSize){
        return "/article/list";
    }

    @GetMapping("man/page")
    @ResponseBody
    public Response page(@RequestParam(value = "status") Integer status,
                         @RequestParam(value = "pageNum") Integer pageNum,
                         @RequestParam(value = "pageSize") Integer pageSize) {
        ArticleExample articleExample = new ArticleExample();
        articleExample.createCriteria().andUserIdEqualTo(1).andStatusEqualTo(status);
        articleExample.setOrderByClause("release_date desc");
        PageInfo page = articleService.selectPageByExample(articleExample, pageNum, pageSize);
        return ResponseUtil.success(page);
    }


    @GetMapping("man/edit")
    public ModelAndView edit(HttpSession session) {
        User user = (User) session.getAttribute("user");
        CategoryExample categoryExample = new CategoryExample();
        categoryExample.createCriteria().andUserIdEqualTo(1);
        List<Category> categories = categoryService.selectByExample(categoryExample);
        List<SysCategory> sysCategories = sysCategoryService.selectAll();
        ModelAndView mv = new ModelAndView("/article/edit");
        //个人分类
        mv.addObject("categories",categories);
        //系统分类
        mv.addObject("sysCategories", sysCategories);
        //文章类型，是否原创
        mv.addObject("types", ArticleTypeEnum.values());
        return mv;
    }

    @PostMapping("man/publish")
    @ResponseBody
    public Response publish(Article article,@RequestParam(value = "cId") List<Integer> cIds, HttpSession session) {
        User user = (User) session.getAttribute("user");
        article.setUserId(1);
        articleService.publish(article,cIds);
        return ResponseUtil.success();
    }

    @PostMapping("man/draft")
    public Response draft(Article article,@RequestParam(value = "cId") List<Integer> cIds, HttpSession session) {
        User user = (User) session.getAttribute("user");
        article.setUserId(1);
        articleService.draft(article, cIds);
        return ResponseUtil.success();
    }
}
