package com.limin.blog.vo;

import com.limin.blog.model.Comment;
import com.limin.blog.model.User;

import java.util.ArrayList;
import java.util.List;

public class CommentVo {
    private Comment comment;
    private User user;
    private List<CommentVo> children = new ArrayList<>();

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<CommentVo> getChildren() {
        return children;
    }

    public void setChildren(List<CommentVo> children) {
        this.children = children;
    }
}
