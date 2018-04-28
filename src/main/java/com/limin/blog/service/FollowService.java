package com.limin.blog.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.limin.blog.enums.ArticleEnum;
import com.limin.blog.enums.EntityEnum;
import com.limin.blog.enums.FollowEnum;
import com.limin.blog.enums.ForumTopicEnum;
import com.limin.blog.mapper.ArticleMapper;
import com.limin.blog.mapper.FollowMapper;
import com.limin.blog.mapper.ForumTopicMapper;
import com.limin.blog.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.parser.Entity;
import java.util.Date;
import java.util.List;

@Service
public class FollowService {
    @Autowired
    private FollowMapper followMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ForumTopicMapper forumTopicMapper;

    public Follow select(Integer userId,Integer entityType,Integer entityId){
        FollowExample example = new FollowExample();
        example.createCriteria().andUserIdEqualTo(userId).andEntityTypeEqualTo(entityType).andEntityIdEqualTo(entityId);
        List<Follow> follows = followMapper.selectByExample(example);
        if (follows!=null&&follows.size()>0) {
            return follows.get(0);
        }
        return null;
    }

    public Follow follow(Integer userId, Integer entityType,Integer entityId){
        Follow follow = select(userId,entityType,entityId);
        if (follow!=null) {
            if (follow.getStatus().equals(FollowEnum.FOLLOW.getVal())){
                return follow;
            } else {
                follow.setStatus(FollowEnum.FOLLOW.getVal());
                followMapper.updateByPrimaryKeySelective(follow);
                return follow;
            }
        } else {
            follow = new Follow();
            follow.setUserId(userId);
            follow.setEntityType(entityType);
            follow.setEntityId(entityId);
            follow.setFollowDate(new Date());
            follow.setStatus(FollowEnum.FOLLOW.getVal());
            followMapper.insertSelective(follow);
        }
        return follow;
    }
    public Follow unfollow(Integer userId, Integer entityType,Integer entityId){
        Follow follow = select(userId,entityType,entityId);
        if (follow!=null) {
            if (follow.getStatus().equals(FollowEnum.UNFOLLOW.getVal())){
                return follow;
            } else {
                follow.setFollowDate(new Date());
                follow.setStatus(FollowEnum.UNFOLLOW.getVal());
                followMapper.updateByPrimaryKeySelective(follow);
                return follow;
            }
        } else {
            follow = new Follow();
            follow.setUserId(userId);
            follow.setEntityType(entityType);
            follow.setEntityId(entityId);
            follow.setFollowDate(new Date());
            follow.setStatus(FollowEnum.UNFOLLOW.getVal());
            followMapper.insertSelective(follow);
        }
        return follow;
    }

    public PageInfo<Follow> selectByUserId(Integer userId, Integer entityType,Integer status,Integer pageNum,Integer pageSize){
        FollowExample example = new FollowExample();
        example.createCriteria().andUserIdEqualTo(userId).andEntityTypeEqualTo(entityType).andStatusEqualTo(status);
        PageHelper.startPage(pageNum,pageSize);
        List<Follow> follows = followMapper.selectByExample(example);
        return new PageInfo<>(follows);
    }
    public PageInfo<Follow> selectByEntityId(Integer entityId, Integer entityType,Integer status,Integer pageNum,Integer pageSize){
        FollowExample example = new FollowExample();
        example.createCriteria().andEntityIdEqualTo(entityId).andEntityTypeEqualTo(entityType).andStatusEqualTo(status);
        PageHelper.startPage(pageNum,pageSize);
        List<Follow> follows = followMapper.selectByExample(example);
        return new PageInfo<>(follows);
    }
    public void black(Integer id, Integer entityId) {
        Follow follow = select(id,EntityEnum.USER.getVal(), entityId);
        //设为黑名单
        if (follow!=null) {
            if (follow.getStatus().equals(FollowEnum.FORBIDDEN.getVal())) {
                return;
            } else {
                follow.setStatus(FollowEnum.FORBIDDEN.getVal());
                followMapper.updateByPrimaryKeySelective(follow);
            }
        } else {
            follow = new Follow();
            follow.setUserId(id);
            follow.setEntityType(EntityEnum.USER.getVal());
            follow.setEntityId(entityId);
            follow.setStatus(FollowEnum.FORBIDDEN.getVal());
            follow.setFollowDate(new Date());
            followMapper.insertSelective(follow);
        }
        //取消关注
        unfollow(entityId,EntityEnum.USER.getVal(),id);
        //取消文章收藏
        ArticleExample articleExample = new ArticleExample();
        articleExample.createCriteria().andUserIdEqualTo(id).andStatusEqualTo(ArticleEnum.PUBLISHED.getVal());
        List<Article> articles = articleMapper.selectByExample(articleExample);
        for (Article article : articles) {
            unfollow(entityId,EntityEnum.ARITCLE.getVal(),article.getId());
        }
        articleExample = new ArticleExample();
        articleExample.createCriteria().andUserIdEqualTo(entityId).andStatusEqualTo(ArticleEnum.PUBLISHED.getVal());
        articles = articleMapper.selectByExample(articleExample);
        for (Article article : articles) {
            unfollow(id,EntityEnum.ARITCLE.getVal(),article.getId());
        }

        //取消帖子收藏
        ForumTopicExample topicExample = new ForumTopicExample();
        topicExample.createCriteria().andUserIdEqualTo(id).andStatusEqualTo(ForumTopicEnum.PUBLISHED.getVal());
        List<ForumTopic> forumTopics = forumTopicMapper.selectByExample(topicExample);
        for (ForumTopic forumTopic: forumTopics) {
            unfollow(entityId,EntityEnum.TOPIC.getVal(),forumTopic.getId());
        }
        topicExample = new ForumTopicExample();
        topicExample.createCriteria().andUserIdEqualTo(entityId).andStatusEqualTo(ForumTopicEnum.PUBLISHED.getVal());
        forumTopics = forumTopicMapper.selectByExample(topicExample);
        for (ForumTopic forumTopic: forumTopics) {
            unfollow(id,EntityEnum.TOPIC.getVal(),forumTopic.getId());
        }
    }
    public boolean blackcheck(Integer fromId,Integer toId) {
        Follow follow = select(fromId, EntityEnum.USER.getVal(), toId);
        if (follow!=null&follow.getStatus().equals(FollowEnum.FORBIDDEN.getVal())) {
            return true;
        }
        return false;
    }
}
