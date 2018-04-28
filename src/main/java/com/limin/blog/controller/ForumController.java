package com.limin.blog.controller;

import com.github.pagehelper.PageInfo;
import com.limin.blog.constant.BlogConst;
import com.limin.blog.enums.EntityEnum;
import com.limin.blog.enums.FollowEnum;
import com.limin.blog.enums.ForumTopicEnum;
import com.limin.blog.model.*;
import com.limin.blog.service.FollowService;
import com.limin.blog.service.ForumService;
import com.limin.blog.util.ResponseUtil;
import com.limin.blog.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("forum")
public class ForumController {

    @Autowired
    private ForumService forumService;

    @Autowired
    private FollowService followService;

    @GetMapping(value = {"","/","/{themeId}"})
    public ModelAndView forum(@PathVariable(value = "themeId",required = false)Integer themeId,
                              @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                              @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize) {
        ModelAndView mv = new ModelAndView("forum/index");
        List<ForumTheme> forumThemes = forumService.selectAllForumTheme();
        mv.addObject("themes",forumThemes);
        ForumTopicExample example = new ForumTopicExample();
        if (themeId!=null){
            example.createCriteria().andThemeIdEqualTo(themeId).andStatusEqualTo(ForumTopicEnum.PUBLISHED.getVal());
        } else {
            example.createCriteria().andStatusEqualTo(ForumTopicEnum.PUBLISHED.getVal());
        }
        example.setOrderByClause("release_date desc");
        PageInfo<ForumTopic> topics = forumService.selectTopicByExample(example, pageNum, pageSize);
        mv.addObject("topics",topics);
        mv.addObject("themeId",themeId);
        return mv;
    }

    @GetMapping(value = "topic/{topicId}")
    public ModelAndView topic(@PathVariable(value = "topicId")Integer topicId){
        ModelAndView mv = new ModelAndView("forum/topic");
        ForumTopic forumTopic = forumService.selectTopicById(topicId);
        mv.addObject("topic",forumTopic);
        return mv;
    }

    @ResponseBody
    @GetMapping(value = "reply")
    public Response reply(@RequestParam(value = "topicId")Integer topicId,
                          @RequestParam(value = "pageNum")Integer pageNum,
                          @RequestParam(value = "pageSize")Integer pageSize){
        PageInfo<ForumReply> forumReplyPageInfo = forumService.selectReplyByTopicId(topicId, pageNum, pageSize);
        return ResponseUtil.success(forumReplyPageInfo);
    }
    @ResponseBody
    @PostMapping(value = "man/reply")
    public Response doreply(@RequestParam(value = "topicId")Integer topicId,
                            @RequestParam(value="content")String content,
                            HttpSession session){
        User user = (User) session.getAttribute(BlogConst.LOGIN_SESSION_KEY);
        ForumTopic forumTopic = forumService.selectTopicById(topicId);
        Follow follow = followService.select(forumTopic.getUserId(), EntityEnum.USER.getVal(), user.getId());
        if (follow!=null&&follow.getStatus().equals(FollowEnum.FORBIDDEN.getVal())) {
            return ResponseUtil.error(2,"你已被对方拉入黑名单");
        }
        forumTopic.setCommentNum(forumTopic.getCommentNum()+1);
        forumService.updateTopicByIdSelective(forumTopic);
        ForumReply reply = new ForumReply();
        reply.setUserId(user.getId());
        reply.setUserName(user.getName());
        reply.setUserHeadUrl(user.getHeadUrl());
        reply.setTopicId(topicId);
        reply.setContent(content);
        reply = forumService.addReply(reply);
        return ResponseUtil.success(reply);
    }

    @GetMapping("man/post")
    public ModelAndView post(){
        ModelAndView mv = new ModelAndView("forum/post");
        List<ForumTheme> forumThemes = forumService.selectAllForumTheme();
        mv.addObject("themes",forumThemes);
        return mv;
    }
    @ResponseBody
    @PostMapping("man/post")
    public Response doPost(ForumTopic forumTopic,HttpSession session){
        User user = (User) session.getAttribute(BlogConst.LOGIN_SESSION_KEY);
        forumTopic.setUserId(user.getId());
        forumTopic.setUserName(user.getName());
        forumTopic.setUserHeadUrl(user.getHeadUrl());
        forumTopic = forumService.addTopic(forumTopic);
        return ResponseUtil.success(forumTopic.getId());
    }
}
