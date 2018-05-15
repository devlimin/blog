package com.limin.blog.controller.admin;

import com.github.pagehelper.PageInfo;
import com.limin.blog.model.UserExample;
import com.limin.blog.service.UserService;
import com.limin.blog.util.ResponseUtil;
import com.limin.blog.vo.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

@Controller(value = "adminUser")
@RequestMapping(value = "admin/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(value = "info")
    public ModelAndView info(){
        ModelAndView mv = new ModelAndView("admin/user/info");
        mv.addObject("type","user/info");
        return mv;
    }

    @ResponseBody
    @GetMapping(value = "page")
    public Response page(@RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                         @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,
                         @RequestParam(value = "name",required = false)String name,
                         @RequestParam(value = "email",required = false)String email,
                         @RequestParam(value = "state",required = false)Integer state,
                         @RequestParam(value = "beginTime",required = false)Long beginTime,
                         @RequestParam(value = "endTime",required = false)Long endTime){
        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(name)) {
            criteria.andNameLike("%"+name+"%");
        }
        if (StringUtils.isNotBlank(email)) {
            criteria.andEmailLike("%"+email+"%");
        }
        if (state!=null) {
            criteria.andStateEqualTo(state);
        }
        if (beginTime!=null) {
            criteria.andBirthGreaterThanOrEqualTo(new Date(beginTime));
        }
        if (endTime!=null) {
            criteria.andBirthLessThanOrEqualTo(new Date(endTime));
        }
        example.setOrderByClause("birth desc");
        PageInfo pageInfo = userService.selectPageByExample(example, pageNum, pageSize);
        return ResponseUtil.success(pageInfo);
    }
}
