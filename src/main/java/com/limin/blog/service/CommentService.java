package com.limin.blog.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.limin.blog.enums.CommentEnum;
import com.limin.blog.exception.BizException;
import com.limin.blog.mapper.CommentMapper;
import com.limin.blog.model.Article;
import com.limin.blog.model.Comment;
import com.limin.blog.model.CommentExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@CacheConfig(cacheNames = "comment")
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private ArticleService articleService;

    public Comment comment(Comment comment) {
        comment.setReleaseDate(new Date());
        comment.setStatus(1);
        comment.setStatus(CommentEnum.PUBLISHED.getVal());
        commentMapper.insertSelective(comment);
        Integer articleId = comment.getArticleId();
        Article article = articleService.selectById(articleId);
        articleService.updateCommentNum(articleId,article.getCommentNum()+1);
        return comment;
    }

    public List<Comment> selectByExample(CommentExample commentExample) {
        return commentMapper.selectByExampleWithBLOBs(commentExample);
    }
    public PageInfo<Comment> selectPageByExampleWithBLOGS(CommentExample commentExample, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Comment> comments = commentMapper.selectByExampleWithBLOBs(commentExample);
        PageInfo page = new PageInfo(comments);
        return page;
    }

    @CacheEvict(key = "'comment:'+#id.toString()")
    public void delete(Integer id) {
        Comment comment = commentMapper.selectByPrimaryKey(id);
        if (comment == null) {
            throw new BizException(2,"不存在该评论");
        }
        comment.setStatus(CommentEnum.DELETED.getVal());
        commentMapper.updateByPrimaryKeySelective(comment);
        Article article = articleService.selectById(comment.getArticleId());
        articleService.updateCommentNum(article.getId(),article.getCommentNum()-1);
    }

    @Cacheable(key = "'comment:'+#cid.toString()")
    public Comment selectById(Integer cid) {
        return commentMapper.selectByPrimaryKey(cid);
    }
}
