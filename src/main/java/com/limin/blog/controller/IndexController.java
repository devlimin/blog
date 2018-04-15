package com.limin.blog.controller;

import com.github.pagehelper.PageInfo;
import com.limin.blog.mapper.SysCategoryMapper;
import com.limin.blog.model.Article;
import com.limin.blog.model.ArticleExample;
import com.limin.blog.model.SysCategory;
import com.limin.blog.model.SysCategoryExample;
import com.limin.blog.service.ArticleService;
import com.limin.blog.util.ResponseUtil;
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
    @ResponseBody
    @GetMapping("/index/page")
    public Response page(@RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                         @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize) {
        ArticleExample example = new ArticleExample();
        example.setOrderByClause("release_date");
        PageInfo<Article> pageInfo = articleService.selectPageByExampleWithBLOBS(example, pageNum, pageSize);
        if (pageInfo.getList().size()>0) {
            for (Article article : pageInfo.getList()) {
                Document document = Jsoup.parseBodyFragment(article.getContent());
                Element body = document.body();
                article.setContent(StringUtils.substring(body.text(), 0, 180));
            }
        }
        return ResponseUtil.success(pageInfo);
    }
}
