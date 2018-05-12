package com.limin.blog.controller.admin;

import com.github.pagehelper.PageInfo;
import com.limin.blog.mapper.SysCategoryMapper;
import com.limin.blog.model.*;
import com.limin.blog.service.ArticleService;
import com.limin.blog.service.CommentService;
import com.limin.blog.service.UserService;
import com.limin.blog.util.ResponseUtil;
import com.limin.blog.vo.ArticleVo;
import com.limin.blog.vo.CommentVo;
import com.limin.blog.vo.Response;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
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
    private SysCategoryMapper sysCategoryMapper;

    @GetMapping(value = "article")
    public ModelAndView article(){
        ModelAndView mv = new ModelAndView("admin/article/article");
        mv.addObject("type","article/article");
        return mv;
    }
    @ResponseBody
    @GetMapping(value = "articlePage")
    public Response articlePage(@RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize){
        PageInfo<ArticleVo> articleVoPageInfo = new PageInfo<>();
        articleVoPageInfo.setList(new ArrayList<>());
        ArticleExample example = new ArticleExample();
        example.setOrderByClause("release_date desc");
        PageInfo<Article> pageInfo = articleService.selectPageByExampleWithBLOBS(example, pageNum, pageSize);
        if (pageInfo.getList().size()>0) {
            for (Article article : pageInfo.getList()) {
                Document document = Jsoup.parseBodyFragment(article.getContent());
                Element body = document.body();
                article.setContent(StringUtils.substring(body.text(), 0, 180));
                User user = userService.selectById(article.getUserId());
                SysCategory sysCategory = sysCategoryMapper.selectByPrimaryKey(article.getSysCateId());
                ArticleVo articleVo = new ArticleVo();
                articleVo.setArticle(article);
                articleVo.setUser(user);
                articleVo.setSysCategory(sysCategory);
                articleVoPageInfo.getList().add(articleVo);
            }
        }
        articleVoPageInfo.setTotal(pageInfo.getTotal());
        return ResponseUtil.success(articleVoPageInfo);
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
                                @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize){
        CommentExample example = new CommentExample();
        example.setOrderByClause("release_date desc");
        PageInfo<Comment> commentPageInfo = commentService.selectPageByExampleWithBLOGS(example, pageNum, pageSize);
        List<CommentVo> commentVos =null;
        if (commentPageInfo.getList()!= null&& commentPageInfo.getList().size()>0) {
            commentVos = new ArrayList<>();
            for (Comment comment:commentPageInfo.getList()) {
                CommentVo commentVo = new CommentVo();
                commentVo.setComment(comment);
                commentVo.setArticle(articleService.selectById(comment.getArticleId()));
                commentVo.setUser(userService.selectById(comment.getUserId()));
                commentVos.add(commentVo);
            }
        }
        PageInfo<CommentVo> commentVoPageInfo = new PageInfo<>(commentVos);
        commentVoPageInfo.setList(commentVos);
        commentVoPageInfo.setTotal(commentPageInfo.getTotal());
        return ResponseUtil.success(commentVoPageInfo);
    }
}
