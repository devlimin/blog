package com.limin.blog.controller;

import com.github.pagehelper.PageInfo;
import com.limin.blog.constant.BlogConst;
import com.limin.blog.enums.EntityEnum;
import com.limin.blog.enums.ForumTopicEnum;
import com.limin.blog.model.*;
import com.limin.blog.service.FollowService;
import com.limin.blog.service.ForumService;
import com.limin.blog.service.SearchService;
import com.limin.blog.util.ResponseUtil;
import com.limin.blog.vo.Response;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("forum")
public class ForumController {

    @Autowired
    private ForumService forumService;

    @Autowired
    private FollowService followService;

    @Autowired
    private SearchService searchService;

    @Autowired
    private RedisTemplate redisTemplate;

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
    public ModelAndView topic(@PathVariable(value = "topicId")Integer topicId,
                              HttpSession session,
                              HttpServletRequest request){
        ModelAndView mv = new ModelAndView("forum/topic");
        ForumTopic forumTopic = forumService.selectTopicById(topicId);
        String remoteAddr = request.getRemoteAddr();
        if(!redisTemplate.opsForSet().isMember("ip:"+remoteAddr,"topic:"+topicId)){
            redisTemplate.opsForSet().add("ip:"+remoteAddr,"topic:"+topicId);
            if (redisTemplate.opsForSet().size("ip:"+remoteAddr)==1) {
                redisTemplate.expire("ip:"+remoteAddr,1, TimeUnit.DAYS);
            }
            forumTopic.setReadNum(forumTopic.getReadNum()+1);
            forumService.updateReadNum(topicId, forumTopic.getReadNum());
        }
        mv.addObject("topic",forumTopic);
        User user = (User) session.getAttribute(BlogConst.LOGIN_SESSION_KEY);
        if (user!=null){
            Follow follow = followService.select(user.getId(), EntityEnum.USER.getVal(), forumTopic.getUserId());
            mv.addObject("follow",follow);
            Follow mark = followService.select(user.getId(),EntityEnum.TOPIC.getVal(),topicId);
            mv.addObject("mark",mark);
        }
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
    @GetMapping(value = "page/{uid}")
    public Response page(@PathVariable(value = "uid")Integer uid,
                        @RequestParam(value = "pageNum")Integer pageNum,
                         @RequestParam(value = "pageSize")Integer pageSize) {
        ForumTopicExample topicExample = new ForumTopicExample();
        topicExample.createCriteria().andUserIdEqualTo(uid).andStatusEqualTo(ForumTopicEnum.PUBLISHED.getVal());
        topicExample.setOrderByClause("release_date desc");
        PageInfo<ForumTopic> pageInfo = forumService.selectTopicByExample(topicExample, pageNum, pageSize);
        if (pageInfo.getList().size()>0) {
            for (ForumTopic topic : pageInfo.getList()) {
                Document document = Jsoup.parseBodyFragment(topic.getContent());
                Element body = document.body();
                topic.setContent(StringUtils.substring(body.text(), 0, 180));
            }
        }
        return ResponseUtil.success(pageInfo);
    }


    @ResponseBody
    @PostMapping(value = "man/reply")
    public Response doreply(@RequestParam(value = "topicId")Integer topicId,
                            @RequestParam(value="content")String content,
                            HttpSession session){
        User user = (User) session.getAttribute(BlogConst.LOGIN_SESSION_KEY);
        ForumTopic forumTopic = forumService.selectTopicById(topicId);
        if(followService.blackcheck(forumTopic.getUserId(),user.getId())){
            return ResponseUtil.error(2,"你已被对方拉入黑名单");
        }
        if (followService.blackcheck(user.getId(),forumTopic.getUserId())) {
            return ResponseUtil.error(2,"对方已被你拉入黑名单");
        }
        forumTopic.setCommentNum(forumTopic.getCommentNum()+1);
        forumService.updateTopicByIdSelective(forumTopic);
        ForumReply reply = new ForumReply();
        reply.setUserId(user.getId());
        reply.setUserName(user.getName());
        reply.setUserHeadUrl(user.getHeadUrl());
        reply.setTopicId(topicId);
        reply.setTopicTitle(forumTopic.getTitle());
        reply.setTopicUserId(forumTopic.getUserId());
        reply.setTopicUserName(forumTopic.getUserName());
        reply.setContent(content);
        reply = forumService.addReply(reply);
        return ResponseUtil.success(reply);
    }

    @GetMapping(value = {"man/post","man/post/{id}"})
    public ModelAndView post(@PathVariable(value = "id",required = false)Integer id){
        ModelAndView mv = new ModelAndView("forum/post");
        if(id != null) {
            ForumTopic forumTopic = forumService.selectTopicById(id);
            mv.addObject("topic",forumTopic);
        }
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
        searchService.indexTopic(forumTopic);
        return ResponseUtil.success(forumTopic.getId());
    }
    @GetMapping("man/list")
    public String list(){
        return "/forum/list";
    }

    @ResponseBody
    @GetMapping("man/page")
    public Response mpage(@RequestParam(value = "status")Integer status,
                         @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                         @RequestParam(value = "pageSize",defaultValue = "2")Integer pageSize,
                          HttpSession session){
        User user = (User) session.getAttribute(BlogConst.LOGIN_SESSION_KEY);
        ForumTopicExample example = new ForumTopicExample();
        example.createCriteria().andStatusEqualTo(status).andUserIdEqualTo(user.getId());
        PageInfo pageInfo = forumService.selectTopicByExample(example, pageNum, pageSize);
        return ResponseUtil.success(pageInfo);
    }

    @ResponseBody
    @PostMapping(value = "man/iscomment")
    public Response iscomment(@RequestParam(value = "id")Integer id){
        ForumTopic forumTopic = forumService.changeTopicCommentStatus(id);
        return ResponseUtil.success(forumTopic.getIsComment());
    }

    @PostMapping("man/del")
    @ResponseBody
    public Response del(
            @RequestParam(value = "id")Integer id) {
        forumService.del(id);
        return ResponseUtil.success();
    }

    @ResponseBody
    @PostMapping("man/deepdel")
    public Response deepdel(
            @RequestParam(value = "id")Integer id) {
        forumService.deepdel(id);
        return ResponseUtil.success();
    }

    @GetMapping("man/reply")
    public String reply(){
        return "/forum/reply";
    }

    @ResponseBody
    @GetMapping("man/replys")
    public Response replys(@RequestParam(value = "status")Integer status,
                           @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                           @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,
                           HttpSession session){
        User user = (User) session.getAttribute(BlogConst.LOGIN_SESSION_KEY);
        PageInfo pageInfo = null;
        if (status == 0){
            ForumReplyExample replyExample = new ForumReplyExample();
            replyExample.createCriteria().andTopicUserIdEqualTo(user.getId()).andStatusEqualTo(ForumTopicEnum.PUBLISHED.getVal());
            replyExample.setOrderByClause("release_date desc");
            pageInfo = forumService.selectReplyByExample(replyExample,pageNum,pageSize);
        } else if (status == 1){
            ForumReplyExample replyExample = new ForumReplyExample();
            replyExample.createCriteria().andUserIdEqualTo(user.getId()).andStatusEqualTo(ForumTopicEnum.PUBLISHED.getVal());
            replyExample.setOrderByClause("release_date desc");
            pageInfo = forumService.selectReplyByExample(replyExample,pageNum,pageSize);
        }
        return ResponseUtil.success(pageInfo);
    }
    @ResponseBody
    @PostMapping(value = "man/replydel")
    public Response replydel(@RequestParam(value = "id")Integer id) {
        forumService.deleteReplyById(id);
        return ResponseUtil.success();
    }
}
