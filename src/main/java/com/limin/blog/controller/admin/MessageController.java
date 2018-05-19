package com.limin.blog.controller.admin;

import com.github.pagehelper.PageInfo;
import com.limin.blog.model.MessageExample;
import com.limin.blog.service.MessageService;
import com.limin.blog.util.ResponseUtil;
import com.limin.blog.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

@Controller("adminMessageController")
@RequestMapping(value = "admin/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

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
}
