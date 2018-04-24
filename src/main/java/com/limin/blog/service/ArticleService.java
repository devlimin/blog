package com.limin.blog.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.limin.blog.enums.ArticleEnum;
import com.limin.blog.exception.BizException;
import com.limin.blog.mapper.ArticleMapper;
import com.limin.blog.model.Article;
import com.limin.blog.model.ArticleCategory;
import com.limin.blog.model.ArticleCategoryExample;
import com.limin.blog.model.ArticleExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@CacheConfig(cacheNames = "article")
public class ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleCategoryService articleCategoryService;

    public PageInfo<Article> selectPageByUserIdAndStatus(Integer userId, Integer status, Integer pageNum,Integer pageSize){
        ArticleExample example = new ArticleExample();
        example.createCriteria().andUserIdEqualTo(userId).andStatusEqualTo(status);
        PageHelper.startPage(pageNum,pageSize);
        List<Article> articles = articleMapper.selectByExample(example);
        return new PageInfo<Article>(articles);
    }

    public PageInfo<Article> selectPageByUserIdAndStatusWithBLOBS(Integer userId, Integer status, Integer pageNum,Integer pageSize){
        ArticleExample example = new ArticleExample();
        example.createCriteria().andUserIdEqualTo(userId).andStatusEqualTo(status);
        example.setOrderByClause("release_date desc");
        PageHelper.startPage(pageNum,pageSize);
        List<Article> articles = articleMapper.selectByExampleWithBLOBs(example);
        return new PageInfo<Article>(articles);
    }

    public PageInfo selectPageByExampleWithBLOBS(ArticleExample articleExample, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Article> articles = articleMapper.selectByExampleWithBLOBs(articleExample);
        return new PageInfo(articles);
    }

    @Cacheable(key = "'article:'+#id.toString()")
    public Article selectById(Integer id) {
        return articleMapper.selectByPrimaryKey(id);
    }

    /**
     * 发表文章
     * @param article
     */
    @CachePut(key = "'article:'+#article.id.toString()",condition = "#article.id!=null")
    public Article publish(Article article,  List<Integer> cIds) {
        //若已存在则修改
        if (article.getId() != null && articleMapper.selectByPrimaryKey(article.getId()) != null) {
            article.setUpdateDate(new Date());
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
        return article;
    }

    /**
     * 保存草稿
     * @param article
     */
    public Article draft(Article article, List<Integer> cIds) {
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
        return article;
    }


    /**
     * 删除文章
     * @param id 文章ID
     * @return
     */
    @CachePut(key = "'article:'+#id.toString()")
    public Article del(Integer id) {
        Article article = new Article();
        article.setId(id);
        article.setStatus(ArticleEnum.GARBAGE.getVal());
        articleMapper.updateByPrimaryKeySelective(article);
        return article;
    }
    @CachePut(key = "'article:'+#id.toString()")
    public Article deepdel(Integer id) {
        Article article = new Article();
        article.setId(id);
        article.setStatus(ArticleEnum.DELETED.getVal());
        articleMapper.updateByPrimaryKeySelective(article);
        return article;
    }

    @CachePut(key = "'article:'+#id.toString()")
    public Article changeIsComment(Integer id) {
        Article article = articleMapper.selectByPrimaryKey(id);
        if (article==null) {
            throw new BizException(2,"不存在该文章！");
        }
        Boolean isComment = article.getIsComment();
        article.setIsComment(!isComment);
        articleMapper.updateByPrimaryKeySelective(article);
        return article;
    }
}
