package com.limin.blog.controller;

import com.github.pagehelper.PageInfo;
import com.limin.blog.constant.BlogConst;
import com.limin.blog.enums.MessageEnum;
import com.limin.blog.model.Message;
import com.limin.blog.model.User;
import com.limin.blog.service.MessageService;
import com.limin.blog.service.UserService;
import com.limin.blog.util.ResponseUtil;
import com.limin.blog.vo.MessageVo;
import com.limin.blog.vo.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping(value = "message")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @GetMapping(value = "man/conversation")
    public ModelAndView conversations(@RequestParam(value = "type",defaultValue = "1")Integer type,
                                  @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                  @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,
                                  HttpSession session){
        User login_user = (User) session.getAttribute(BlogConst.LOGIN_SESSION_KEY);
        PageInfo<Message> conversations = messageService.conversations(login_user.getId(), pageNum, pageSize);
        List<MessageVo> messageVos = new ArrayList<>();
        if (conversations.getList()!=null&& conversations.getList().size()>0) {
            for (Message message : conversations.getList()) {
                User user = null;
                if(message.getUserId().equals(login_user.getId())){
                    user = userService.selectById(message.getToUserId());
                } else {
                    user = userService.selectById(message.getUserId());
                }
                long count = messageService.conversationUnreadCount(login_user.getId(), message.getConversationId());
                message.setContent(StringUtils.substring(message.getContent(), 0, 50));
                MessageVo messageVo = new MessageVo();
                messageVo.setMessage(message);
                messageVo.setUser(user);
                messageVo.setCount(count);
                messageVos.add(messageVo);
            }
        }
        PageInfo messageVoPageInfo = new PageInfo();
        messageVoPageInfo.setList(messageVos);
        messageVoPageInfo.setTotal(conversations.getTotal());
        ModelAndView mv = new ModelAndView("message/conversation");
        mv.addObject("messageVos",messageVoPageInfo);
        mv.addObject("type",type);
        return mv;
    }

    @GetMapping(value = "man/detail")
    public ModelAndView detail(@RequestParam(value = "conversationId")String conversationId,
                               @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                               @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,
                               HttpSession session){
        ModelAndView mv = new ModelAndView("message/detail");
        User login_user = (User) session.getAttribute(BlogConst.LOGIN_SESSION_KEY);
        PageInfo<Message> messagePageInfo = messageService.conversationDetails(conversationId, pageNum, pageSize);
        List<MessageVo> messageVos = new ArrayList<>();
        if (messagePageInfo.getList()!=null&& messagePageInfo.getList().size()>0) {
            for (Message message : messagePageInfo.getList()) {
                User user = userService.selectById(message.getUserId());
                if (!user.getId().equals(login_user.getId())){
                    mv.addObject("toUserId",user.getId());
                }
                MessageVo messageVo = new MessageVo();
                messageVo.setMessage(message);
                messageVo.setUser(user);
                messageVos.add(messageVo);

            }
        }

        mv.addObject("messageVos",messageVos);
        return mv;
    }

    @ResponseBody
    @PostMapping(value = "man/add")
    public Response add(@RequestParam(value = "toUserId")Integer toUserId,
                        @RequestParam(value = "content")String content,
                        HttpSession session){
        User login_user = (User) session.getAttribute(BlogConst.LOGIN_SESSION_KEY);
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
        messageService.add(message);
        MessageVo messageVo = new MessageVo();
        messageVo.setMessage(message);
        messageVo.setUser(login_user);
        return ResponseUtil.success(messageVo);
    }
}
