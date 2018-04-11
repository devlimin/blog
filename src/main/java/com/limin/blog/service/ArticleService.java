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

    public List<Article> selectPageByExampleWithBLOBS(ArticleExample articleExample, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return articleMapper.selectByExampleWithBLOBs(articleExample);
    }

    /**
     * 发表文章
     * @param article
     */
    public void publish(Article article,  List<Integer> cIds) {
        //发表状态
        article.setStatus(ArticleEnum.PUBLISHED.getVal());
        //默认公开
        if(article.getIsPrivate() == null) {
            article.setIsPrivate(false);
        }
        //默认能评论
        if (article.getIsComment() == null) {
            article.setIsComment(true);
        }
        //若已存在则修改
        if (article.getId() != null && articleMapper.selectByPrimaryKey(article.getId()) != null) {
            articleMapper.updateByPrimaryKeySelective(article);
        }
        //新增文章
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
    }

    /**
     * 保存草稿
     * @param article
     */
    public void draft(Article article, List<Integer> cIds) {
        article.setStatus(ArticleEnum.DRAFT.getVal());
        //新文章直接保存
        if (articleMapper.selectByPrimaryKey(article.getId()) == null) {
            article.setReleaseDate(new Date());
            article.setReadNum(0);
            article.setCommentNum(0);
            articleMapper.insert(article);
        } else {
            //旧文章更新文章
            articleMapper.updateByPrimaryKeyWithBLOBs(article);
        }
    }


    /**
     * 删除文章
     * @param id 文章ID
     * @return
     */
    public boolean delete(Integer id) {
        Article article = articleMapper.selectByPrimaryKey(id);
        if (article == null) {
            return false;
        }
        article.setStatus(ArticleEnum.DELETED.getVal());
        articleMapper.updateByPrimaryKeySelective(article);
        return true;
    }

    /**
     * 能否评论
     * @param id
     * @return
     */
    public boolean changeCommentStatus(Integer id) {
        Article article = articleMapper.selectByPrimaryKey(id);
        if (article == null) {
            return false;
        }
        if (article.getIsComment() == false) {
            article.setIsComment(true);
        } else {
            article.setIsComment(false);
        }
        articleMapper.updateByPrimaryKey(article);
        return true;
    }
}
