package com.limin.blog.vo;

import com.limin.blog.model.Article;
import com.limin.blog.model.Follow;
import com.limin.blog.model.User;

public class FollowVo {
    private Follow follow;
    private User user;
    private Article article;

    public Follow getFollow() {
        return follow;
    }

    public void setFollow(Follow follow) {
        this.follow = follow;
    }

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
}
