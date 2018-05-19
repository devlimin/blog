package com.limin.blog.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.limin.blog.enums.ArticleEnum;
import com.limin.blog.exception.BizException;
import com.limin.blog.mapper.ArticleMapper;
import com.limin.blog.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@CacheConfig(cacheNames = "article")
public class ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private SensitiveService sensitiveService;

    @Autowired
    private ArticleCategoryService articleCategoryService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SearchService searchService;

    public List<Map> selectArchive(Integer userId){
        return articleMapper.selectArchive(userId);
    }

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

    public PageInfo selectPageByExample(ArticleExample articleExample, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Article> articles = articleMapper.selectByExample(articleExample);
        return new PageInfo(articles);
    }

    @Cacheable(key = "'article:'+#id.toString()")
    public Article selectById(Integer id) {
        return articleMapper.selectByPrimaryKey(id);
    }
    public Article detailById(Integer id) {
        return articleMapper.selectByPrimaryKey(id);
    }

    /**
     * 发表文章
     * @param article
     */
    @CacheEvict(key = "'article:'+#article.id.toString()",condition = "#article.id!=null")
    public Article publish(Article article,  List<Integer> cIds) {
        article.setContent(sensitiveService.filter(article.getContent()));
        article.setTitle(sensitiveService.filter(article.getTitle()));
        //发表状态
        article.setStatus(ArticleEnum.PUBLISHED.getVal());
        //若已存在则修改
        if (article.getId() != null && articleMapper.selectByPrimaryKey(article.getId()) != null) {
            article.setUpdateDate(new Date());
            articleMapper.updateByPrimaryKeySelective(article);
        } else {
            article.setIsComment(true);
            article.setReleaseDate(new Date());
            article.setReadNum(0);
            article.setCommentNum(0);
            //新增文章
            articleMapper.insert(article);
            searchService.indexArticle(article);
        }
        //修改文章个人分类
        ArticleCategoryExample example = new ArticleCategoryExample();
        example.createCriteria().andArticleIdEqualTo(article.getId());
        List<ArticleCategory> articleCategories = articleCategoryService.select(example);
        if(articleCategories!=null && articleCategories.size()>0){
            for (ArticleCategory articleCategory: articleCategories){
                Category category = categoryService.selectById(articleCategory.getCategoryId());
                category.setArticleNum(category.getArticleNum()-1);
                categoryService.update(category);
            }
        }
        articleCategoryService.deleteByExample(example);
        if (cIds != null && cIds.size() > 0) {
            for (Integer id : cIds) {
                ArticleCategory articleCategory = new ArticleCategory();
                articleCategory.setArticleId(article.getId());
                articleCategory.setCategoryId(id);
                articleCategoryService.add(articleCategory);
                Category category = categoryService.selectById(id);
                category.setArticleNum(category.getArticleNum()+1);
                categoryService.update(category);
            }
        }
        return article;
    }

    /**
     * 保存草稿
     * @param article
     */
    public Article draft(Article article, List<Integer> cIds) {
        article.setContent(sensitiveService.filter(article.getContent()));
        article.setTitle(sensitiveService.filter(article.getTitle()));
        article.setStatus(ArticleEnum.DRAFT.getVal());
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
    @CacheEvict(key = "'article:'+#id.toString()")
    public void del(Integer id) {
        Article article = new Article();
        article.setId(id);
        article.setStatus(ArticleEnum.GARBAGE.getVal());
        articleMapper.updateByPrimaryKeySelective(article);
    }
    @CacheEvict(key = "'article:'+#id.toString()")
    public void deepdel(Integer id) {
        Article article = new Article();
        article.setId(id);
        article.setStatus(ArticleEnum.DELETED.getVal());
        articleMapper.updateByPrimaryKeySelective(article);
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
    @CachePut(key = "'article:'+#id.toString()")
    public void updateCommentNum(Integer id, Integer commentNum){
        Article article = new Article();
        article.setId(id);
        article.setCommentNum(commentNum);
        articleMapper.updateByPrimaryKeySelective(article);
    }

    public void updateReadNum(Integer id, int readNum) {
        Article article = new Article();
        article.setId(id);
        article.setReadNum(readNum);
        articleMapper.updateByPrimaryKeySelective(article);
    }
}
