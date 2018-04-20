package com.limin.blog.vo;

import com.limin.blog.model.Article;
import com.limin.blog.model.SysCategory;
import com.limin.blog.model.User;

public class ArticleVo {
    private Article article;
    private User user;
    private SysCategory sysCategory;

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public SysCategory getSysCategory() {
        return sysCategory;
    }

    public void setSysCategory(SysCategory sysCategory) {
        this.sysCategory = sysCategory;
    }
}
