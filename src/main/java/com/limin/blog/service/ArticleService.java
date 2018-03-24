package com.limin.blog.service;

import com.github.pagehelper.PageHelper;
import com.limin.blog.enums.ArticleEnum;
import com.limin.blog.mapper.ArticleMapper;
import com.limin.blog.model.Article;
import com.limin.blog.model.ArticleExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    public Article selectById(Integer id) {
        return articleMapper.selectByPrimaryKey(id);
    }

    public List<Article> selectByExample(ArticleExample articleExample) {
        return articleMapper.selectByExample(articleExample);
    }

    public List<Article> selectByExampleWithBLOBS(ArticleExample articleExample) {
        return articleMapper.selectByExampleWithBLOBs(articleExample);
    }

    public List<Article> selectPageByExample(ArticleExample articleExample, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return articleMapper.selectByExample(articleExample);
    }

    public List<Article> selectPageByExampleWithBLOBS(ArticleExample articleExample, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return articleMapper.selectByExampleWithBLOBs(articleExample);
    }

    /**
     * 发表文章
     * @param article
     */
    public int publish(Article article) {
        if (article.getTitle() == null) {

        }
        if (article.getContent() == null) {

        }
        if (article.getSysCateId() == null) {

        }
        article.setStatus(ArticleEnum.PUBLISHED.getKey());
        //若已存在，更改文章状态
        if (article.getId() != null && articleMapper.selectByPrimaryKey(article.getId()) != null) {
            return articleMapper.updateByPrimaryKeySelective(article);
        }
        article.setReleaseDate(new Date());
        article.setReadNum(0);
        article.setCommentNum(0);
        return articleMapper.insert(article);
    }

    /**
     * 保存草稿
     * @param article
     */
    public void draft(Article article) {
        article.setStatus(ArticleEnum.DRAFT.getKey());
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
        article.setStatus(ArticleEnum.DELETED.getKey());
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
