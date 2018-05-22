package com.limin.blog.controller.admin;

import com.github.pagehelper.PageInfo;
import com.limin.blog.model.ForumReply;
import com.limin.blog.model.ForumReplyExample;
import com.limin.blog.model.ForumTopic;
import com.limin.blog.model.ForumTopicExample;
import com.limin.blog.service.ForumService;
import com.limin.blog.util.ResponseUtil;
import com.limin.blog.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

@Controller("adminForumController")
@RequestMapping("admin/forum")
public class ForumController {

    @Autowired
    private ForumService forumService;

    @GetMapping(value = "topic")
    public ModelAndView topic(){
        ModelAndView mv = new ModelAndView("admin/forum/topic");
        mv.addObject("type","forum/topic");
        return mv;
    }

    @ResponseBody
    @GetMapping(value = "topicPage")
    public Response topicPage(@RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                              @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,
                              @RequestParam(value = "tid",required = false)Integer tid,
                              @RequestParam(value = "uid",required = false)Integer uid,
                              @RequestParam(value = "state",required = false)Integer state,
                              @RequestParam(value = "beginTime",required = false)Long beginTime,
                              @RequestParam(value = "endTime",required = false)Long endTime){
        ForumTopicExample example = new ForumTopicExample();
        ForumTopicExample.Criteria criteria = example.createCriteria();
        if (tid!=null) {
            criteria.andIdEqualTo(tid);
        }
        if (uid!=null) {
            criteria.andUserIdEqualTo(uid);
        }
        if (state!=null) {
            criteria.andStatusEqualTo(state);
        }
        if (beginTime!=null) {
            criteria.andReleaseDateGreaterThanOrEqualTo(new Date(beginTime));
        }
        if (endTime!=null) {
            criteria.andReleaseDateLessThanOrEqualTo(new Date(endTime));
        }
        example.setOrderByClause("release_date desc");
        PageInfo pageInfo = forumService.selectTopicByExample(example, pageNum, pageSize);
        return ResponseUtil.success(pageInfo);
    }
    @ResponseBody
    @GetMapping(value = "topicInfo")
    public Response topicInfo(@RequestParam(value = "id")Integer id){
        ForumTopic topic = forumService.selectTopicById(id);
        return ResponseUtil.success(topic);
    }

    @ResponseBody
    @PostMapping(value = "updateTopicStatus")
    public Response updateTopicStatus(@RequestParam(value = "topicId")Integer topicId,
                                         @RequestParam(value = "status")Integer status){
        forumService.updateTopicStatus(topicId,status);
        return ResponseUtil.success();
    }

    @GetMapping(value = "reply")
    public ModelAndView reply(){
        ModelAndView mv = new ModelAndView("admin/forum/reply");
        mv.addObject("type","forum/reply");
        return mv;
    }

    @ResponseBody
    @GetMapping(value = "replyPage")
    public Response replyPage(@RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                              @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,
                              @RequestParam(value = "tid",required = false)Integer tid,
                              @RequestParam(value = "uid",required = false)Integer uid,
                              @RequestParam(value = "state",required = false)Integer state,
                              @RequestParam(value = "beginTime",required = false)Long beginTime,
                              @RequestParam(value = "endTime",required = false)Long endTime){
        ForumReplyExample example = new ForumReplyExample();
        ForumReplyExample.Criteria criteria = example.createCriteria();
        if (tid!=null) {
            criteria.andTopicIdEqualTo(tid);
        }
        if (uid!=null) {
            criteria.andUserIdEqualTo(uid);
        }
        if (state!=null) {
            criteria.andStatusEqualTo(state);
        }
        if (beginTime!=null) {
            criteria.andReleaseDateGreaterThanOrEqualTo(new Date(beginTime));
        }
        if (endTime!=null) {
            criteria.andReleaseDateLessThanOrEqualTo(new Date(endTime));
        }
        example.setOrderByClause("release_date desc");
        PageInfo pageInfo = forumService.selectReplyByExample(example, pageNum, pageSize);
        return ResponseUtil.success(pageInfo);
    }

    @ResponseBody
    @GetMapping(value = "replyInfo")
    public Response replyInfo(@RequestParam(value = "id")Integer id){
        ForumReply reply = forumService.selectReplyById(id);
        return ResponseUtil.success(reply);
    }
    @ResponseBody
    @PostMapping(value = "updateReplyStatus")
    public Response updateReplyStatus(@RequestParam(value = "replyId")Integer replyId,
                                      @RequestParam(value = "status")Integer status){
        forumService.updateReplyStatus(replyId,status);
        return ResponseUtil.success();
    }
}
