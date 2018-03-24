package com.limin.blog.controller;

import com.limin.blog.model.Article;
import com.limin.blog.model.Comment;
import com.limin.blog.model.CommentExample;
import com.limin.blog.service.ArticleService;
import com.limin.blog.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private ArticleService articleService;

    //我文章的评论
    @GetMapping("man/list/in")
    public String listIn() {
        CommentExample commentExample = new CommentExample();
        commentExample.setOrderByClause("release_date desc");
        commentExample.createCriteria().andToUserIdEqualTo(1);
        for(Comment comment : commentService.selectPageByExampleWithBLOGS(commentExample, 1, 10)) {
            Article article = articleService.selectById(comment.getEntityId());
        }
        return "comment/list";
    }

    //我发表的评论
    @GetMapping("man/list/out")
    public String listOut() {
        CommentExample commentExample = new CommentExample();
        commentExample.setOrderByClause("release_date desc");
        commentExample.createCriteria().andUserIdEqualTo(1);
        for (Comment comment : commentService.selectPageByExampleWithBLOGS(commentExample, 1, 10)) {
            Article article = articleService.selectById(comment.getEntityId());
        }
        return "comment/list";
    }
}
