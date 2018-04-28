package com.limin.blog.controller;

import com.github.pagehelper.PageInfo;
import com.limin.blog.constant.BlogConst;
import com.limin.blog.enums.*;
import com.limin.blog.model.*;
import com.limin.blog.service.*;
import com.limin.blog.util.ResponseUtil;
import com.limin.blog.vo.Response;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
    private SysCategoryService sysCategoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private FollowService followService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 查看文章
     * @param id 文章ID
     * @return
     */
    @GetMapping(value = "detail/{id}")
    public ModelAndView article(@PathVariable("id") Integer id,
                                HttpSession session){
        ModelAndView mv = new ModelAndView("article/detail");
        //文章
        Article article = articleService.selectById(id);
        mv.addObject("article",article);
        User user = userService.selectById(article.getUserId());
        mv.addObject("user",user);
        //文章相关的个人分类
        List<Category> articleCategories = new ArrayList<Category>();
        ArticleCategoryExample articleCategoryExample = new ArticleCategoryExample();
        articleCategoryExample.createCriteria().andArticleIdEqualTo(id);
        for(ArticleCategory articleCategory : articleCategoryService.select(articleCategoryExample)) {
            Category category = categoryService.selectById(articleCategory.getCategoryId());
            articleCategories.add(category);
        }
        mv.addObject("articleCategories",articleCategories);

        user = (User) session.getAttribute(BlogConst.LOGIN_SESSION_KEY);
        if (user!=null){
            Follow follow = followService.select(user.getId(), EntityEnum.USER.getVal(), article.getUserId());
            mv.addObject("follow",follow);
            Follow mark = followService.select(user.getId(),EntityEnum.ARITCLE.getVal(),article.getId());
            mv.addObject("mark",mark);
        }
        return mv;
    }

    /**
     * 查找用户文章，按时间降序
     * @param uid 用户ID
     * @return
     */
    @GetMapping(value = {"list/{uid}","list/{uid}/{cid}"})
    public ModelAndView articles(@PathVariable("uid") Integer uid,
                                 @PathVariable(value = "cid",required = false)Integer cid,
                                 HttpSession session){
        ModelAndView mv = new ModelAndView("article/articles");
        mv.addObject("uid",uid);
        mv.addObject("cid",cid);
        User user = userService.selectById(uid);
        mv.addObject("user",user);
        if(cid==null) {
            mv.addObject("title","全部");
        } else {
            Category category = categoryService.selectById(cid);
            mv.addObject("title",category==null?"":category.getName());
        }
        //所有个人分类
        CategoryExample categoryExample = new CategoryExample();
        categoryExample.createCriteria().andUserIdEqualTo(uid).andStatusEqualTo(CategoryEnum.PUBLISHED.getVal());
        List<Category> categories = categoryService.selectByExample(categoryExample);
        mv.addObject("categories",categories);

        user = (User) session.getAttribute(BlogConst.LOGIN_SESSION_KEY);
        if (user!=null){
            Follow follow = followService.select(user.getId(), EntityEnum.USER.getVal(), uid);
            mv.addObject("follow",follow);
        }
        return mv;
    }
    @GetMapping(value = {"page/{uid}","page/{uid}/{cid}"})
    @ResponseBody
    public Response page(@PathVariable(value = "uid") Integer uid,
                         @PathVariable(value = "cid",required = false) Integer cid,
                         @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                         @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize) {
        PageInfo<Article> pageInfo = null;
        if (cid==null) {
            ArticleExample articleExample = new ArticleExample();
            articleExample.createCriteria().andUserIdEqualTo(uid).andStatusEqualTo(ArticleEnum.PUBLISHED.getVal());
            articleExample.setOrderByClause("release_date desc");
            pageInfo = articleService.selectPageByUserIdAndStatusWithBLOBS(uid,ArticleEnum.PUBLISHED.getVal(),pageNum,pageSize);
        } else {
            Category category = categoryService.selectById(cid);
            if (category==null || !category.getUserId().equals(uid)) {
                return ResponseUtil.error(1,"该博主没有该个人分类");
            } else {
                ArticleCategoryExample example =new ArticleCategoryExample();
                example.createCriteria().andCategoryIdEqualTo(cid);
                PageInfo<ArticleCategory> articleCategories = articleCategoryService.selectPageByExample(example,pageNum,pageSize);
                List<Article> articles = new ArrayList<>();
                for (ArticleCategory articleCategory : articleCategories.getList()) {
                    Article article = articleService.selectById(articleCategory.getArticleId());
                    articles.add(article);
                }
                pageInfo = new PageInfo(articles);
                pageInfo.setTotal(articleCategories.getTotal());
            }
        }
        if (pageInfo.getList().size()>0) {
            for (Article article : pageInfo.getList()) {
                Document document = Jsoup.parseBodyFragment(article.getContent());
                Element body = document.body();
                article.setContent(StringUtils.substring(body.text(), 0, 180));
            }
        }
        return ResponseUtil.success(pageInfo);
    }

    //全部
    @GetMapping("man/list")
    public String list(Integer pageNum, Integer pageSize){
        return "/article/list";
    }

    @GetMapping("man/page")
    @ResponseBody
    public Response page(@RequestParam(value = "status") Integer status,
                         @RequestParam(value = "pageNum") Integer pageNum,
                         @RequestParam(value = "pageSize") Integer pageSize,
                         HttpSession session) {
        User user = (User) session.getAttribute(BlogConst.LOGIN_SESSION_KEY);
        PageInfo page = articleService.selectPageByUserIdAndStatus(user.getId(),status, pageNum, pageSize);
        return ResponseUtil.success(page);
    }


    @GetMapping(value = {"man/edit/{id}","man/edit"})
    public ModelAndView edit(
            @PathVariable(value = "id",required = false)Integer id,
            HttpSession session) {
        ModelAndView mv = new ModelAndView("/article/edit");
        if (id != null) {
            Article article = articleService.selectById(id);
            mv.addObject("article",article);
            ArticleCategoryExample example = new ArticleCategoryExample();
            example.createCriteria().andArticleIdEqualTo(id);
            List<Integer> cId = new ArrayList<>();
            for(ArticleCategory category : articleCategoryService.select(example)) {
                cId.add(category.getCategoryId());
            }
            mv.addObject("uCategories", cId);
        }
        User user = (User) session.getAttribute(BlogConst.LOGIN_SESSION_KEY);
        CategoryExample categoryExample = new CategoryExample();
        categoryExample.createCriteria().andUserIdEqualTo(user.getId()).andStatusEqualTo(CategoryEnum.PUBLISHED.getVal());
        //个人分类
        List<Category> categories = categoryService.selectByExample(categoryExample);
        mv.addObject("categories",categories);
        //系统分类
        List<SysCategory> sysCategories = sysCategoryService.selectAll();
        mv.addObject("sysCategories", sysCategories);
        //文章类型，是否原创
        mv.addObject("types", ArticleTypeEnum.values());
        return mv;
    }

    @PostMapping("man/del")
    @ResponseBody
    public Response del(
            @RequestParam(value = "id")Integer id) {
        articleService.del(id);
        return ResponseUtil.success();
    }

    @ResponseBody
    @PostMapping("man/deepdel")
    public Response deepdel(
            @RequestParam(value = "id")Integer id) {
        articleService.deepdel(id);
        return ResponseUtil.success();
    }

    @ResponseBody
    @PostMapping("man/iscomment")
    public Response iscomment(@RequestParam(value = "id")Integer id){
        Article article = articleService.changeIsComment(id);
        return ResponseUtil.success(article.getIsComment());
    }

    @PostMapping("man/publish")
    @ResponseBody
    public Response publish(Article article,@RequestParam(value = "cId",required = false) List<Integer> cIds, HttpSession session) {
        User user = (User) session.getAttribute(BlogConst.LOGIN_SESSION_KEY);
        article.setUserId(user.getId());
        article = articleService.publish(article, cIds);
        return ResponseUtil.success(article.getId());
    }

    @PostMapping("man/draft")
    @ResponseBody
    public Response draft(Article article,@RequestParam(value = "cId",required = false) List<Integer> cIds, HttpSession session) {
        User user = (User) session.getAttribute(BlogConst.LOGIN_SESSION_KEY);
        article.setUserId(user.getId());
        article = articleService.draft(article, cIds);
        return ResponseUtil.success(article.getId());
    }
}
