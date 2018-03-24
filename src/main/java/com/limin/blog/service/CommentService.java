package com.limin.blog.service;

import com.limin.blog.enums.CommentEnum;
import com.limin.blog.mapper.CommentMapper;
import com.limin.blog.model.Comment;
import com.limin.blog.model.CommentExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.Date;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    public List<Comment> selectByEntity(Integer entityType, Integer entityId) {
        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria().andEntityTypeEqualTo(entityType).andEntityIdEqualTo(entityId);
        return commentMapper.selectByExampleWithBLOBs(commentExample);
    }

    public List<Comment> selectByEntityWithBlog(Integer entityType, Integer entityId) {
        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria().andEntityTypeEqualTo(entityType).andEntityIdEqualTo(entityId);
        return commentMapper.selectByExampleWithBLOBs(commentExample);
    }

    public List<Comment> selectPageByEntityWithBlog(Integer entityType, Integer entityId, Integer pageNum, Integer pageSize) {
        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria().andEntityTypeEqualTo(entityType).andEntityIdEqualTo(entityId);
        return commentMapper.selectByExampleWithBLOBs(commentExample);
    }

    public List<Comment> selectByExample(CommentExample commentExample) {
        return commentMapper.selectByExampleWithBLOBs(commentExample);
    }
    public List<Comment> selectPageByExampleWithBLOGS(CommentExample commentExample, Integer pageNum, Integer pageSize) {
        return commentMapper.selectByExampleWithBLOBs(commentExample);
    }

    public void add(Comment comment, Integer entityType, Integer entityId) {
        comment.setEntityType(entityType);
        comment.setEntityId(entityId);
        comment.setReleaseDate(new Date());
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));//过滤标签
        commentMapper.insertSelective(comment);
    }


    public void delete(Integer id) {
        Comment comment = commentMapper.selectByPrimaryKey(id);
        if (comment == null) {

        }
        comment.setStatus(CommentEnum.DELETED.getKey());
        commentMapper.updateByPrimaryKeySelective(comment);
    }

}
