package com.limin.blog.controller.admin;

import com.github.pagehelper.PageInfo;
import com.limin.blog.model.*;
import com.limin.blog.service.*;
import com.limin.blog.util.ResponseUtil;
import com.limin.blog.vo.ArticleVo;
import com.limin.blog.vo.CommentVo;
import com.limin.blog.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.List;

@Controller(value = "adminArticle")
@RequestMapping(value = "admin/article")
public class ArticleController {
    @Autowired
    private ArticleService articleService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;
    @Autowired
    private SysCategoryService sysCategoryService;
    @Autowired
    private CategoryService categoryService;

    @GetMapping(value = "article")
    public ModelAndView article(){
        ModelAndView mv = new ModelAndView("admin/article/article");
        mv.addObject("type","article/article");
        return mv;
    }
    @ResponseBody
    @GetMapping(value = "articlePage")
    public Response articlePage(@RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,
                                @RequestParam(value = "aid",required = false)Integer aid,
                                @RequestParam(value = "uid",required = false)Integer uid,
                                @RequestParam(value = "state",required = false)Integer state,
                                @RequestParam(value = "beginTime",required = false)Long beginTime,
                                @RequestParam(value = "endTime",required = false)Long endTime){

        ArticleExample example = new ArticleExample();
        ArticleExample.Criteria criteria = example.createCriteria();
        if (aid!=null) {
            criteria.andIdEqualTo(aid);
        }
        if (uid!=null) {
            criteria.andUserIdEqualTo(uid);
        }
        if (state!=null) {
            criteria.andStatusEqualTo(state);
        }
        if (beginTime!=null) {
            criteria.andReleaseDateGreaterThanOrEqualTo(new Date(beginTime));
        }
        if (endTime!=null) {
            criteria.andReleaseDateLessThanOrEqualTo(new Date(endTime));
        }
        example.setOrderByClause("release_date desc");
        PageInfo<Article> pageInfo = articleService.selectPageByExample(example, pageNum, pageSize);
        return ResponseUtil.success(pageInfo);
    }
    @ResponseBody
    @GetMapping(value = "articleInfo")
    public Response articleInfo(@RequestParam(value = "id")Integer id){
        Article article = articleService.selectById(id);
        if (article==null) {
            return ResponseUtil.error(2,"不存在该文章");
        }
        User user = userService.selectById(article.getUserId());
        SysCategory sysCategory = sysCategoryService.selectById(article.getSysCateId());
        List<Category> categories = categoryService.selectByArticleId(article.getId());
        ArticleVo articleVo = new ArticleVo();
        articleVo.setArticle(article);
        articleVo.setUser(user);
        articleVo.setSysCategory(sysCategory);
        articleVo.setCategories(categories);
        return ResponseUtil.success(articleVo);
    }

    @ResponseBody
    @PostMapping(value = "updateArticleStatus")
    public Response updateArticleStatus(@RequestParam(value = "articleId")Integer articleId,
                                        @RequestParam(value = "status")Integer status){
        articleService.updateStatus(articleId,status);
        return ResponseUtil.success();
    }

    @GetMapping(value = "comment")
    public ModelAndView comment(){
        ModelAndView mv = new ModelAndView("admin/article/comment");
        mv.addObject("type","article/comment");
        return mv;
    }
    @ResponseBody
    @GetMapping(value = "commentPage")
    public Response commentPage(@RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,
                                @RequestParam(value = "aid",required = false)Integer aid,
                                @RequestParam(value = "uid",required = false)Integer uid,
                                @RequestParam(value = "state",required = false)Integer state,
                                @RequestParam(value = "beginTime",required = false)Long beginTime,
                                @RequestParam(value = "endTime",required = false)Long endTime){
        CommentExample example = new CommentExample();
        CommentExample.Criteria criteria = example.createCriteria();
        if (aid!=null) {
            criteria.andArticleIdEqualTo(aid);
        }
        if (uid!=null) {
            criteria.andUserIdEqualTo(uid);
        }
        if (state!=null) {
            criteria.andStatusEqualTo(state);
        }
        if (beginTime!=null) {
            criteria.andReleaseDateGreaterThanOrEqualTo(new Date(beginTime));
        }
        if (endTime!=null) {
            criteria.andReleaseDateLessThanOrEqualTo(new Date(endTime));
        }
        example.setOrderByClause("release_date desc");
        PageInfo<Comment> pageInfo = commentService.selectPageByExampleWithBLOGS(example, pageNum, pageSize);
        return ResponseUtil.success(pageInfo);
    }
    @ResponseBody
    @GetMapping(value = "commentInfo")
    public Response commentInfo(@RequestParam(value = "id")Integer id){
        Comment comment = commentService.selectById(id);
        User user = userService.selectById(comment.getUserId());
        Article article = articleService.selectById(comment.getArticleId());
        CommentVo commentVo = new CommentVo();
        commentVo.setComment(comment);
        commentVo.setUser(user);
        commentVo.setArticle(article);;
        return ResponseUtil.success(commentVo);
    }

    @ResponseBody
    @PostMapping(value = "updateCommentStatus")
    public Response updateCommentStatus(@RequestParam(value = "commentId")Integer commentId,
                                        @RequestParam(value = "status")Integer status){
        commentService.updateStatus(commentId,status);
        return ResponseUtil.success();
    }

    @GetMapping(value = "category")
    public ModelAndView category(){
        ModelAndView mv = new ModelAndView("admin/article/category");
        mv.addObject("type","article/category");
        return mv;
    }
    @ResponseBody
    @GetMapping(value = "categoryPage")
    public Response categoryPage(@RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,
                                @RequestParam(value = "cid",required = false)Integer cid,
                                @RequestParam(value = "uid",required = false)Integer uid,
                                @RequestParam(value = "state",required = false)Integer state){
        CategoryExample example = new CategoryExample();
        CategoryExample.Criteria criteria = example.createCriteria();
        if (cid!=null) {
            criteria.andIdEqualTo(cid);
        }
        if (uid!=null) {
            criteria.andUserIdEqualTo(uid);
        }
        if (state!=null) {
            criteria.andStatusEqualTo(state);
        }
        PageInfo pageInfo = categoryService.selectByExample(example,pageNum,pageSize);
        return ResponseUtil.success(pageInfo);
    }
    @ResponseBody
    @PostMapping(value = "updateCategoryStatus")
    public Response updateCategoryStatus(@RequestParam(value = "categoryId")Integer categoryId,
                                        @RequestParam(value = "status")Integer status){
        categoryService.updateStatus(categoryId,status);
        return ResponseUtil.success();
    }

    @GetMapping(value = "sysCategory")
    public ModelAndView sysCategory(){
        ModelAndView mv = new ModelAndView("admin/article/sysCategory");
        mv.addObject("type","article/sysCategory");
        return mv;
    }

    @ResponseBody
    @GetMapping(value = "sysCategoryPage")
    public Response sysCategoryPage(@RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                 @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,
                                 @RequestParam(value = "cid",required = false)Integer cid,
                                 @RequestParam(value = "state",required = false)Integer state){
        SysCategoryExample example = new SysCategoryExample();
        SysCategoryExample.Criteria criteria = example.createCriteria();
        if (cid!=null) {
            criteria.andIdEqualTo(cid);
        }
        if (state!=null) {
            criteria.andStatusEqualTo(state);
        }
        PageInfo pageInfo = sysCategoryService.selectByExample(example,pageNum,pageSize);
        return ResponseUtil.success(pageInfo);
    }

    @ResponseBody
    @GetMapping(value = "sysDisable")
    public Response sysDisable(@RequestParam(value = "id",required = false)Integer id){
        sysCategoryService.disable(id);
        return ResponseUtil.success();
    }

    @ResponseBody
    @GetMapping(value = "sysEnable")
    public Response sysEnable(@RequestParam(value = "id",required = false)Integer id){
        sysCategoryService.enable(id);
        return ResponseUtil.success();
    }
    @ResponseBody
    @PostMapping(value = "sysAdd")
    public Response sysAdd(@RequestParam(value = "name",required = false)String name){
        SysCategory sysCategory = sysCategoryService.add(name);
        return ResponseUtil.success(sysCategory.getId());
    }

    @ResponseBody
    @PostMapping(value = "updateSCName")
    public Response updateSCName(@RequestParam(value = "scId")Integer scId,
                                 @RequestParam(value = "name")String name){
        sysCategoryService.updateName(scId,name);
        return ResponseUtil.success();
    }
}
