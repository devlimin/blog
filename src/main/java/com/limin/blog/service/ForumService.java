package com.limin.blog.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.limin.blog.enums.ForumTopicEnum;
import com.limin.blog.mapper.ForumReplyMapper;
import com.limin.blog.mapper.ForumThemeMapper;
import com.limin.blog.mapper.ForumTopicMapper;
import com.limin.blog.model.*;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.Date;
import java.util.List;

@Service
public class ForumService {
    @Autowired
    private ForumThemeMapper forumThemeMapper;
    @Autowired
    private ForumTopicMapper forumTopicMapper;
    @Autowired
    private ForumReplyMapper forumReplyMapper;
    @Autowired
    private UserService userService;

    @Autowired
    private SensitiveService sensitiveService;

    public List<ForumTheme> selectAllForumTheme(){
        ForumThemeExample example = new ForumThemeExample();
        example.setOrderByClause("id asc");
        return forumThemeMapper.selectByExample(example);
    }

    public ForumTheme selectThemeById(Integer id){
        return forumThemeMapper.selectByPrimaryKey(id);
    }

    public ForumTopic addTopic(ForumTopic forumTopic){
        forumTopic.setStatus(ForumTopicEnum.PUBLISHED.getVal());
        forumTopic.setCommentNum(0);
        forumTopic.setReadNum(0);
        forumTopic.setReleaseDate(new Date());
        forumTopic.setContent(sensitiveService.filter(forumTopic.getContent()));
        forumTopic.setContent(Jsoup.clean(forumTopic.getContent(), Whitelist.relaxed()));
        forumTopic.setThemeName(selectThemeById(forumTopic.getThemeId()).getName());
        forumTopicMapper.insertSelective(forumTopic);
        return forumTopic;
    }
    public ForumTopic selectTopicById(Integer id){
        return forumTopicMapper.selectByPrimaryKey(id);
    }
    public PageInfo selectTopicByUserId(Integer userId, Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        ForumTopicExample example = new ForumTopicExample();
        example.createCriteria().andUserIdEqualTo(userId);
        example.setOrderByClause("release_date desc");
        List<ForumTopic> forumTopics = forumTopicMapper.selectByExample(example);
        return new PageInfo(forumTopics);
    }
    public PageInfo selectTopicByExample(ForumTopicExample example,Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<ForumTopic> forumTopics = forumTopicMapper.selectByExample(example);
        return new PageInfo(forumTopics);
    }

    public ForumReply addReply(ForumReply forumReply) {
        forumReply.setStatus(ForumTopicEnum.PUBLISHED.getVal());
        forumReply.setContent(HtmlUtils.htmlEscape(sensitiveService.filter(forumReply.getContent())));
        forumReply.setReleaseDate(new Date());
        forumReplyMapper.insert(forumReply);
        return forumReply;
    }
    public PageInfo<ForumReply> selectReplyByTopicId(Integer topicId, Integer pageNum, Integer pageSize){
        ForumReplyExample example = new ForumReplyExample();
        example.createCriteria().andTopicIdEqualTo(topicId).andStatusEqualTo(ForumTopicEnum.PUBLISHED.getVal());
        example.setOrderByClause("release_date desc");
        PageHelper.startPage(pageNum,pageSize);
        List<ForumReply> forumReplies = forumReplyMapper.selectByExample(example);
        return new PageInfo<ForumReply>(forumReplies);
    }

    public void updateTopicByIdSelective(ForumTopic forumTopic) {
        forumTopicMapper.updateByPrimaryKeySelective(forumTopic);
    }
}
