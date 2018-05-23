package com.limin.blog.controller.admin;

import com.github.pagehelper.PageInfo;
import com.limin.blog.constant.BlogConst;
import com.limin.blog.enums.MessageEnum;
import com.limin.blog.model.AdminUser;
import com.limin.blog.model.Message;
import com.limin.blog.model.MessageExample;
import com.limin.blog.model.User;
import com.limin.blog.service.MessageService;
import com.limin.blog.service.UserService;
import com.limin.blog.util.ResponseUtil;
import com.limin.blog.vo.MessageVo;
import com.limin.blog.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.Date;

@Controller("adminMessageController")
@RequestMapping(value = "admin/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @GetMapping(value = "msg")
    public ModelAndView msg(){
        ModelAndView mv = new ModelAndView("admin/message/msg");
        mv.addObject("type","message/msg");
        return mv;
    }

    @ResponseBody
    @GetMapping(value = "msgPage")
    public Response articlePage(@RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,
                                @RequestParam(value = "aid",required = false)Integer aid,
                                @RequestParam(value = "uid",required = false)Integer uid,
                                @RequestParam(value = "toUid",required = false)Integer toUid,
                                @RequestParam(value = "state",required = false)Integer state,
                                @RequestParam(value = "beginTime",required = false)Long beginTime,
                                @RequestParam(value = "endTime",required = false)Long endTime) {
        MessageExample example = new MessageExample();
        MessageExample.Criteria criteria = example.createCriteria();
        if (aid!=null) {
            criteria.andIdEqualTo(aid);
        }
        if (uid!=null) {
            criteria.andUserIdEqualTo(uid);
        }
        if(toUid!=null) {
            criteria.andToUserIdEqualTo(toUid);
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
        PageInfo pageInfo = messageService.selectByExample(example,pageNum,pageSize);
        return ResponseUtil.success(pageInfo);
    }

    @ResponseBody
    @GetMapping(value = "msgInfo")
    public Response replyInfo(@RequestParam(value = "id")Integer id){
        Message message = messageService.selectById(id);
        User user = userService.selectById(message.getUserId());
        User toUser = userService.selectById(message.getToUserId());
        MessageVo messageVo = new MessageVo();
        messageVo.setMessage(message);
        messageVo.setUser(user);
        messageVo.setToUser(toUser);
        return ResponseUtil.success(messageVo);
    }

    @ResponseBody
    @PostMapping(value = "updateMsgStatus")
    public Response updateReplyStatus(@RequestParam(value = "msgId")Integer msgId,
                                      @RequestParam(value = "status")Integer status){
        messageService.updateStatus(msgId,status);
        return ResponseUtil.success();
    }

    @GetMapping(value = "send")
    public ModelAndView send(){
        ModelAndView mv = new ModelAndView("admin/message/send");
        mv.addObject("type","message/send");
        return mv;
    }
    @ResponseBody
    @PostMapping(value = "add")
    public Response add(@RequestParam(value = "toUserId")Integer toUserId,
                        @RequestParam(value = "content")String content,
                        HttpSession session){
        AdminUser login_user = (AdminUser) session.getAttribute(BlogConst.LOGIN_ADMIN_KEY);
        Message message = new Message();
        message.setUserId(login_user.getId());
        message.setToUserId(toUserId);
        message.setContent(content);
        message.setReleaseDate(new Date());
        if (toUserId<login_user.getId()) {
            message.setConversationId(toUserId+"-"+login_user.getId());
        } else {
            message.setConversationId(login_user.getId()+"-"+toUserId);
        }
        message.setIsRead(MessageEnum.UNREAD.getVal());
        message.setStatus(MessageEnum.PUBLISHED.getVal());
        message.setType(MessageEnum.ADMIN.getVal());
        messageService.add(message);
        return ResponseUtil.success();
    }
}
