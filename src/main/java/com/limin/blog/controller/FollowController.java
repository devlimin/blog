package com.limin.blog.controller;

import com.github.pagehelper.PageInfo;
import com.limin.blog.constant.BlogConst;
import com.limin.blog.enums.EntityEnum;
import com.limin.blog.enums.FollowEnum;
import com.limin.blog.model.Article;
import com.limin.blog.model.Follow;
import com.limin.blog.model.ForumTopic;
import com.limin.blog.model.User;
import com.limin.blog.service.ArticleService;
import com.limin.blog.service.FollowService;
import com.limin.blog.service.ForumService;
import com.limin.blog.service.UserService;
import com.limin.blog.util.ResponseUtil;
import com.limin.blog.vo.FollowVo;
import com.limin.blog.vo.Response;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("follow")
public class FollowController {
    @Autowired
    private FollowService followService;
    @Autowired
    private UserService userService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private ForumService forumService;


    @ResponseBody
    @PostMapping(value = "man/followuser")
    public Response followUser(HttpSession session,
                           @RequestParam(value = "entityId")Integer entityId){
        User user = (User) session.getAttribute(BlogConst.LOGIN_SESSION_KEY);
        Follow follow = followService.select(entityId, EntityEnum.USER.getVal(), user.getId());
        if (follow!=null&&follow.getStatus().equals(FollowEnum.FORBIDDEN.getVal())) {
            return ResponseUtil.error(2,"你已被对方拉入黑名单");
        }
        followService.follow(user.getId(), EntityEnum.USER.getVal(),entityId);
        return ResponseUtil.success();
    }

    @ResponseBody
    @PostMapping(value = "man/unfollowuser")
    public Response unfollow(HttpSession session,
                             @RequestParam(value = "entityId")Integer entityId){
        User user = (User) session.getAttribute(BlogConst.LOGIN_SESSION_KEY);
        followService.unfollow(user.getId(), EntityEnum.USER.getVal(),entityId);
        return ResponseUtil.success();
    }

    @GetMapping(value = "man/ifollow")
    public ModelAndView follers(@RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,
                                HttpSession session){
        ModelAndView mv = new ModelAndView("follow/user");
        User user = (User) session.getAttribute(BlogConst.LOGIN_SESSION_KEY);
        PageInfo<Follow> follows = followService.selectByUserId(user.getId(), EntityEnum.USER.getVal(), FollowEnum.FOLLOW.getVal(), pageNum, pageSize);
        PageInfo<FollowVo> pageInfo = new PageInfo<>();
        pageInfo.setPageSize(follows.getPageSize());
        pageInfo.setPageNum(follows.getPageNum());
        pageInfo.setTotal(follows.getTotal());
        List<FollowVo> list = new ArrayList<>();
        if (follows.getList()!=null && follows.getList().size()>0) {
            for(Follow follow:follows.getList()) {
                user = userService.selectById(follow.getEntityId());
                FollowVo followVo = new FollowVo();
                followVo.setFollow(follow);
                followVo.setUser(user);
                list.add(followVo);
            }
        }
        pageInfo.setList(list);
        mv.addObject("pageInfo",pageInfo);
        mv.addObject("type","ifollow");
        return mv;
    }
    @GetMapping(value = "man/followi")
    public ModelAndView unfollers(@RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,
                                HttpSession session){
        ModelAndView mv = new ModelAndView("follow/user");
        User user = (User) session.getAttribute(BlogConst.LOGIN_SESSION_KEY);
        PageInfo<Follow> follows = followService.selectByEntityId(user.getId(), EntityEnum.USER.getVal(), FollowEnum.FOLLOW.getVal(), pageNum, pageSize);
        PageInfo<FollowVo> pageInfo = new PageInfo<>();
        pageInfo.setPageSize(follows.getPageSize());
        pageInfo.setPageNum(follows.getPageNum());
        pageInfo.setTotal(follows.getTotal());
        List<FollowVo> list = new ArrayList<>();
        if (follows.getList()!=null && follows.getList().size()>0) {
            for(Follow follow:follows.getList()) {
                user = userService.selectById(follow.getUserId());
                FollowVo followVo = new FollowVo();
                followVo.setFollow(follow);
                followVo.setUser(user);
                list.add(followVo);
            }
        }
        pageInfo.setList(list);
        mv.addObject("pageInfo",pageInfo);
        mv.addObject("type","followi");
        return mv;
    }
    @GetMapping(value = "man/iblack")
    public ModelAndView iblack(@RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                               @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,
                               HttpSession session){
        ModelAndView mv = new ModelAndView("follow/user");
        User user = (User) session.getAttribute(BlogConst.LOGIN_SESSION_KEY);
        PageInfo<Follow> follows = followService.selectByUserId(user.getId(), EntityEnum.USER.getVal(), FollowEnum.FORBIDDEN.getVal(), pageNum, pageSize);
        PageInfo<FollowVo> pageInfo = new PageInfo<>();
        pageInfo.setPageSize(follows.getPageSize());
        pageInfo.setPageNum(follows.getPageNum());
        pageInfo.setTotal(follows.getTotal());
        List<FollowVo> list = new ArrayList<>();
        if (follows.getList()!=null && follows.getList().size()>0) {
            for(Follow follow:follows.getList()) {
                user = userService.selectById(follow.getEntityId());
                FollowVo followVo = new FollowVo();
                followVo.setFollow(follow);
                followVo.setUser(user);
                list.add(followVo);
            }
        }
        pageInfo.setList(list);
        mv.addObject("pageInfo",pageInfo);
        mv.addObject("type","iblack");

        return mv;
    }

    @ResponseBody
    @PostMapping(value = "man/black")
    public Response black(@RequestParam(value = "entityId")Integer entityId,
                          HttpSession session) {
        User user = (User) session.getAttribute(BlogConst.LOGIN_SESSION_KEY);
        followService.black(user.getId(),entityId);
        return ResponseUtil.success();
    }
    @ResponseBody
    @PostMapping(value = "man/white")
    public Response white(@RequestParam(value = "entityId")Integer entityId,
                          HttpSession session) {
        User user = (User) session.getAttribute(BlogConst.LOGIN_SESSION_KEY);
        followService.unfollow(user.getId(),EntityEnum.USER.getVal(),entityId);
        return ResponseUtil.success();
    }
    @ResponseBody
    @PostMapping(value = "man/markArticle")
    public Response followArticle(HttpSession session,
                               @RequestParam(value = "entityId")Integer entityId){
        User user = (User) session.getAttribute(BlogConst.LOGIN_SESSION_KEY);
        Article article = articleService.selectById(entityId);
        if (article!=null){
            if(followService.blackcheck(article.getUserId(),user.getId())){
                return ResponseUtil.error(2,"你已被对方拉入黑名单");
            }
            if (followService.blackcheck(user.getId(),article.getUserId())) {
                return ResponseUtil.error(2,"对方已被你拉入黑名单");
            }
        } else {
            return ResponseUtil.error(2,"没有这篇帖子");
        }

        followService.follow(user.getId(), EntityEnum.ARITCLE.getVal(),entityId);
        return ResponseUtil.success();
    }

    @ResponseBody
    @PostMapping(value = "man/unmarkArticle")
    public Response unfollowArticle(HttpSession session,
                                  @RequestParam(value = "entityId")Integer entityId){
        User user = (User) session.getAttribute(BlogConst.LOGIN_SESSION_KEY);
        followService.unfollow(user.getId(), EntityEnum.ARITCLE.getVal(),entityId);
        return ResponseUtil.success();
    }

    @ResponseBody
    @PostMapping(value = "man/markTopic")
    public Response markTopic(HttpSession session,
                                  @RequestParam(value = "entityId")Integer entityId){
        User user = (User) session.getAttribute(BlogConst.LOGIN_SESSION_KEY);
        ForumTopic forumTopic = forumService.selectTopicById(entityId);
        if (forumTopic!=null) {
            if(followService.blackcheck(forumTopic.getUserId(),user.getId())){
                return ResponseUtil.error(2,"你已被对方拉入黑名单");
            }
            if(followService.blackcheck(user.getId(),forumTopic.getUserId())){
                return ResponseUtil.error(2,"你已被对方拉入黑名单");
            }
        } else{
            return ResponseUtil.error(2,"没有这篇帖子");
        }

        followService.follow(user.getId(), EntityEnum.TOPIC.getVal(),entityId);
        return ResponseUtil.success();
    }

    @ResponseBody
    @PostMapping(value = "man/unmarkTopic")
    public Response unmarkTopic(HttpSession session,
                                    @RequestParam(value = "entityId")Integer entityId){
        User user = (User) session.getAttribute(BlogConst.LOGIN_SESSION_KEY);
        followService.unfollow(user.getId(), EntityEnum.TOPIC.getVal(),entityId);
        return ResponseUtil.success();
    }

    @GetMapping("man/article")
    public ModelAndView markArticles(@RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                     @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,
                                     HttpSession session){
        ModelAndView mv = new ModelAndView("follow/mark");
        User user = (User) session.getAttribute(BlogConst.LOGIN_SESSION_KEY);
        PageInfo<Follow> follows = followService.selectByUserId(user.getId(), EntityEnum.ARITCLE.getVal(), FollowEnum.FOLLOW.getVal(), pageNum, pageSize);
        PageInfo<FollowVo> pageInfo = new PageInfo<>();
        pageInfo.setPageNum(follows.getPageNum());
        pageInfo.setPageSize(follows.getPageSize());
        pageInfo.setTotal(follows.getTotal());
        List<FollowVo> list = new ArrayList<>();
        if (follows.getList()!=null&&follows.getList().size()>0) {
            for (Follow follow:follows.getList()) {
                Article article = articleService.selectById(follow.getEntityId());
                Document document = Jsoup.parseBodyFragment(article.getContent());
                Element body = document.body();
                article.setContent(StringUtils.substring(body.text(), 0, 180));
                user = userService.selectById(article.getUserId());
                FollowVo followVo = new FollowVo();
                followVo.setFollow(follow);
                followVo.setUser(user);
                followVo.setArticle(article);
                list.add(followVo);
            }
        }
        pageInfo.setList(list);
        mv.addObject("pageInfo",pageInfo);
        mv.addObject("type","article");
        return mv;
    }
}
