package com.limin.blog.controller;

import com.github.pagehelper.PageInfo;
import com.limin.blog.enums.ArticleEnum;
import com.limin.blog.enums.SysCategoryEnum;
import com.limin.blog.mapper.SysCategoryMapper;
import com.limin.blog.model.*;
import com.limin.blog.service.ArticleService;
import com.limin.blog.service.SearchService;
import com.limin.blog.service.SysCategoryService;
import com.limin.blog.service.UserService;
import com.limin.blog.util.ResponseUtil;
import com.limin.blog.vo.ArticleVo;
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

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/")
public class IndexController {

    @Autowired
    SysCategoryMapper sysCategoryMapper;

    @Autowired
    SysCategoryService sysCategoryService;

    @Autowired
    ArticleService articleService;

    @Autowired
    UserService userService;

    @Autowired
    SearchService searchService;

    @GetMapping({"/","index"})
    public ModelAndView index(@RequestParam(value = "sysCateId",defaultValue = "-1")Integer sysCateId){
        ModelAndView mv = new ModelAndView("index");
        SysCategoryExample sysCategoryExample = new SysCategoryExample();
        sysCategoryExample.createCriteria().andStatusEqualTo(SysCategoryEnum.PUBLISHED.getVal());
        sysCategoryExample.setOrderByClause("id asc");
        List<SysCategory> sysCategories=  sysCategoryMapper.selectByExample(sysCategoryExample);
        mv.addObject("sysCategories", sysCategories);
        mv.addObject("sysCateId",sysCateId);
        return mv;
    }
    @ResponseBody
    @GetMapping("/index/page")
    public Response page(@RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                         @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,
                         @RequestParam(value = "sysCateId",defaultValue = "-1")Integer sysCateId) {
        PageInfo<ArticleVo> articleVoPageInfo = new PageInfo<>();
        articleVoPageInfo.setList(new ArrayList<>());
        ArticleExample example = new ArticleExample();
        if (sysCateId!=-1){
            example.createCriteria().andStatusEqualTo(ArticleEnum.PUBLISHED.getVal()).andSysCateIdEqualTo(sysCateId);
        } else{
            example.createCriteria().andStatusEqualTo(ArticleEnum.PUBLISHED.getVal());
        }
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

    @GetMapping(value = "search")
    private ModelAndView search(@RequestParam(value = "keywords")String keywords,
                                @RequestParam(value = "type",defaultValue = "article")String type){
        ModelAndView mv = new ModelAndView("search");
        mv.addObject("keywords",keywords);
        mv.addObject("type",type);
        return mv;
    }

    @ResponseBody
    @GetMapping(value = "searcharticle")
    public Response searcharticle(@RequestParam(value = "keywords")String keywords,
                                @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize){
        PageInfo<Article> pageInfo = searchService.searchArticle(keywords, pageNum, pageSize);
        PageInfo<ArticleVo> articleVoPageInfo = new PageInfo<>();
        articleVoPageInfo.setList(new ArrayList<>());
        if (pageInfo.getList().size()>0) {
            for (Article article : pageInfo.getList()) {
                Article temp = articleService.selectById(article.getId());
                article.setReadNum(temp.getReadNum());
                article.setCommentNum(temp.getCommentNum());
                ArticleVo articleVo = new ArticleVo();
                articleVo.setArticle(article);
                User user = userService.selectById(temp.getUserId());
                articleVo.setUser(user);
                articleVoPageInfo.getList().add(articleVo);
            }
        }
        articleVoPageInfo.setTotal(pageInfo.getTotal());
        return ResponseUtil.success(articleVoPageInfo);
    }

    @ResponseBody
    @GetMapping(value = "searchTopic")
    public Response searchTopic(@RequestParam(value = "keywords")String keywords,
                             @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                             @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize){
        PageInfo<ForumTopic> forumTopicPageInfo = searchService.searchTopic(keywords, pageNum, pageSize);
        return ResponseUtil.success(forumTopicPageInfo);
    }
}
