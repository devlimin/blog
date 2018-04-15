package com.limin.blog.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.limin.blog.enums.ArticleEnum;
import com.limin.blog.mapper.ArticleMapper;
import com.limin.blog.model.Article;
import com.limin.blog.model.ArticleCategory;
import com.limin.blog.model.ArticleCategoryExample;
import com.limin.blog.model.ArticleExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleCategoryService articleCategoryService;


    public Article selectById(Integer id) {
        return articleMapper.selectByPrimaryKey(id);
    }

    public List<Article> selectByExample(ArticleExample articleExample) {
        return articleMapper.selectByExample(articleExample);
    }

    public List<Article> selectByExampleWithBLOBS(ArticleExample articleExample) {
        return articleMapper.selectByExampleWithBLOBs(articleExample);
    }

    public PageInfo selectPageByExample(ArticleExample articleExample, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Article> articles = articleMapper.selectByExample(articleExample);
        return new PageInfo(articles);
    }

    public PageInfo selectPageByExampleWithBLOBS(ArticleExample articleExample, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Article> articles = articleMapper.selectByExampleWithBLOBs(articleExample);
        return new PageInfo(articles);
    }

    /**
     * 发表文章
     * @param article
     */
    public Integer publish(Article article,  List<Integer> cIds) {

        //若已存在则修改
        if (article.getId() != null && articleMapper.selectByPrimaryKey(article.getId()) != null) {
            articleMapper.updateByPrimaryKeySelective(article);
        } else {
            //发表状态
            article.setStatus(ArticleEnum.PUBLISHED.getVal());
            article.setIsPrivate(false);
            article.setIsComment(true);
            article.setReleaseDate(new Date());
            article.setReadNum(0);
            article.setCommentNum(0);
            //新增文章
            articleMapper.insert(article);
        }

        //修改文章个人分类
        ArticleCategoryExample example = new ArticleCategoryExample();
        example.createCriteria().andArticleIdEqualTo(article.getId());
        articleCategoryService.deleteByExample(example);
        if (cIds != null && cIds.size() > 0) {
            for (Integer id : cIds) {
                ArticleCategory articleCategory = new ArticleCategory();
                articleCategory.setArticleId(article.getId());
                articleCategory.setCategoryId(id);
                articleCategoryService.add(articleCategory);
            }
        }
        return article.getId();
    }

    /**
     * 保存草稿
     * @param article
     */
    public Integer draft(Article article, List<Integer> cIds) {
        article.setStatus(ArticleEnum.DRAFT.getVal());
        article.setIsPrivate(false);
        article.setIsComment(true);
        article.setReleaseDate(new Date());
        article.setReadNum(0);
        article.setCommentNum(0);
        articleMapper.insert(article);
        //修改文章个人分类
        ArticleCategoryExample example = new ArticleCategoryExample();
        example.createCriteria().andArticleIdEqualTo(article.getId());
        articleCategoryService.deleteByExample(example);
        if (cIds != null && cIds.size() > 0) {
            for (Integer id : cIds) {
                ArticleCategory articleCategory = new ArticleCategory();
                articleCategory.setArticleId(article.getId());
                articleCategory.setCategoryId(id);
                articleCategoryService.add(articleCategory);
            }
        }
        return article.getId();
    }


    /**
     * 删除文章
     * @param id 文章ID
     * @return
     */
    public boolean del(Integer id) {
        Article article = new Article();
        article.setId(id);
        article.setStatus(ArticleEnum.GARBAGE.getVal());
        articleMapper.updateByPrimaryKeySelective(article);
        return true;
    }

    public boolean deepdel(Integer id) {
        Article article = new Article();
        article.setId(id);
        article.setStatus(ArticleEnum.DELETED.getVal());
        articleMapper.updateByPrimaryKeySelective(article);
        return true;
    }

    public void comment(Integer id, boolean comment) {
        Article article = new Article();
        article.setId(id);
        article.setIsComment(comment);
        articleMapper.updateByPrimaryKeySelective(article);
    }
}
