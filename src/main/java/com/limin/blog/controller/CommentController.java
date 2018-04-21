package com.limin.blog.controller;

import com.github.pagehelper.PageInfo;
import com.limin.blog.constant.BlogConst;
import com.limin.blog.enums.CommentEnum;
import com.limin.blog.model.Article;
import com.limin.blog.model.Comment;
import com.limin.blog.model.CommentExample;
import com.limin.blog.model.User;
import com.limin.blog.service.ArticleService;
import com.limin.blog.service.CommentService;
import com.limin.blog.service.UserService;
import com.limin.blog.util.ResponseUtil;
import com.limin.blog.vo.CommentVo;
import com.limin.blog.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private ArticleService articleService;

    @ResponseBody
    @GetMapping(value = "list")
    public Response list(@RequestParam(value = "aid") Integer aid,
                         @RequestParam(value = "pageNum") Integer pageNum,
                         @RequestParam(value = "pageSize") Integer pageSize){
        CommentExample example = new CommentExample();
        example.createCriteria().andArticleIdEqualTo(aid).
                andPidEqualTo(-1).andStatusEqualTo(CommentEnum.PUBLISHED.getVal());
        example.setOrderByClause("release_date desc");
        //父级评论
        PageInfo<Comment> comments = commentService.selectPageByExampleWithBLOGS(example, pageNum, pageSize);
        PageInfo<CommentVo> commentVos = new PageInfo<>();
        commentVos.setTotal(comments.getTotal());
        commentVos.setList(new ArrayList<>());
        if(comments.getList() != null && comments.getList().size() > 0) {
            for(Comment comment:comments.getList()) {
                CommentVo commentVo = new CommentVo();
                commentVo.setComment(comment);
                commentVo.setUser(userService.selectById(comment.getUserId()));

                CommentExample subexample = new CommentExample();
                subexample.createCriteria().andArticleIdEqualTo(comment.getArticleId()).
                        andPidEqualTo(comment.getId()).andStatusEqualTo(CommentEnum.PUBLISHED.getVal());
                subexample.setOrderByClause("release_date asc");
                //子评论
                List<Comment> subcomments = commentService.selectByExample(subexample);
                for(Comment subcomment:subcomments) {
                    CommentVo subCommentVo = new CommentVo();
                    subCommentVo.setComment(subcomment);
                    subCommentVo.setUser(userService.selectById(subcomment.getUserId()));
                    commentVo.getChildren().add(subCommentVo);
                }
                commentVos.getList().add(commentVo);
            }
        }
        return ResponseUtil.success(commentVos);
    }

    @ResponseBody
    @PostMapping("man/comment")
    public Response comment(@RequestParam(value = "content")String content,
                            @RequestParam(value = "aid")Integer aid,//文章id
                            @RequestParam(value = "cid",defaultValue = "-1")Integer cid,//评论id
                            @RequestParam(value = "pid",defaultValue = "-1")Integer pid,
                            HttpSession session) {
        Article article = articleService.selectById(aid);
        if (!article.getIsComment()){
            return ResponseUtil.error(2,"该文章已禁止评论");
        }
        User user = (User) session.getAttribute(BlogConst.LOGIN_SESSION_KEY);
        Comment comment = new Comment();
        comment.setArticleId(aid);
        comment.setContent(content);
        comment.setUserId(user.getId());
        comment.setToUserId(articleService.selectById(aid).getUserId());
        comment.setPid(pid);
        comment = commentService.comment(comment);
        CommentVo commentVo = new CommentVo();
        commentVo.setComment(comment);
        commentVo.setUser(userService.selectById(user.getId()));
        return ResponseUtil.success(commentVo);
    }
    
    @ResponseBody
    @PostMapping(value = "man/quickcomment")
    public Response quickcomment(@RequestParam(value = "cid")Integer cid,
                                 @RequestParam(value = "content")String content,
                                 HttpSession session){
        User user = (User) session.getAttribute(BlogConst.LOGIN_SESSION_KEY);
        Comment comment = commentService.selectById(cid);
        Comment quickcomment = new Comment();
        quickcomment.setUserId(user.getId());
        quickcomment.setContent(content);
        quickcomment.setToUserId(comment.getToUserId());
        quickcomment.setArticleId(comment.getArticleId());
        if (comment.getPid() == -1) {
            quickcomment.setPid(comment.getId());
        } else {
            quickcomment.setPid(comment.getPid());
        }
        quickcomment = commentService.comment(quickcomment);
        return ResponseUtil.success(quickcomment);
    }

    //我文章的评论
    @GetMapping("man/list")
    public String list() {
        return "comment/list";
    }

    @ResponseBody
    @GetMapping(value = "man/page")
    public Response page(@RequestParam(value = "status")Integer status,
                         @RequestParam(value = "pageNum")Integer pageNum,
                         @RequestParam(value = "pageSize")Integer pageSize,
                         HttpSession session){
        User user = (User) session.getAttribute(BlogConst.LOGIN_SESSION_KEY);
        CommentExample example = new CommentExample();
        if (status==0) {
            example.createCriteria().andToUserIdEqualTo(user.getId()).andStatusEqualTo(CommentEnum.PUBLISHED.getVal());
        } else {
            example.createCriteria().andUserIdEqualTo(user.getId()).andStatusEqualTo(CommentEnum.PUBLISHED.getVal());
        }
        example.setOrderByClause("release_date desc");
        PageInfo<Comment> commentPageInfo = commentService.selectPageByExampleWithBLOGS(example, pageNum, pageSize);
        List<CommentVo> commentVos =null;
        if (commentPageInfo.getList()!= null&& commentPageInfo.getList().size()>0) {
            commentVos = new ArrayList<>();
            for (Comment comment:commentPageInfo.getList()) {
                CommentVo commentVo = new CommentVo();
                commentVo.setComment(comment);
                commentVo.setArticle(articleService.selectById(comment.getArticleId()));
                if (status==0) {
                    commentVo.setUser(userService.selectById(comment.getUserId()));
                } else if (status==1) {
                    commentVo.setUser(userService.selectById(commentVo.getArticle().getUserId()));
                }
                commentVos.add(commentVo);
            }
        }
        PageInfo<CommentVo> commentVoPageInfo = new PageInfo<>(commentVos);
        commentVoPageInfo.setList(commentVos);
        commentVoPageInfo.setTotal(commentPageInfo.getTotal());
        return ResponseUtil.success(commentVoPageInfo);
    }

    @ResponseBody
    @PostMapping(value = "man/del")
    public Response del(@RequestParam(value = "id")Integer id) {
        commentService.delete(id);
        return ResponseUtil.success();
    }
}
