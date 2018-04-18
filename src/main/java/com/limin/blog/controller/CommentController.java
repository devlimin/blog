package com.limin.blog.controller;

import com.github.pagehelper.PageInfo;
import com.limin.blog.model.Comment;
import com.limin.blog.model.CommentExample;
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
        example.createCriteria().andArticleIdEqualTo(aid).andPidEqualTo(-1);
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
                subexample.createCriteria().andArticleIdEqualTo(comment.getArticleId()).andPidEqualTo(comment.getId());
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
    @PostMapping("comment")
    public Response comment(@RequestParam(value = "content")String content,
                            @RequestParam(value = "aid")Integer aid,//文章id
                            @RequestParam(value="cid")Integer cid,//评论id
                            @RequestParam(value = "pid",defaultValue = "-1")Integer pid,
                            HttpSession session) {
        Comment comment = new Comment();
        comment.setArticleId(aid);
        comment.setContent(content);
        comment.setUserId(1);
        comment.setPid(pid);
        comment = commentService.comment(comment);
        CommentVo commentVo = new CommentVo();
        commentVo.setComment(comment);
        commentVo.setUser(userService.selectById(1));
        return ResponseUtil.success(commentVo);
    }
    
    

    //我文章的评论
    @GetMapping("man/list/in")
    public String listIn() {

        return "comment/list";
    }

    //我发表的评论
    @GetMapping("man/list/out")
    public String listOut() {

        return "comment/list";
    }
}
