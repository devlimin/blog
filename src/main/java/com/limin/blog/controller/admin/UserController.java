package com.limin.blog.controller.admin;

import com.github.pagehelper.PageInfo;
import com.limin.blog.model.UserExample;
import com.limin.blog.service.UserService;
import com.limin.blog.util.ResponseUtil;
import com.limin.blog.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

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
                         @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize){
        UserExample example = new UserExample();
        example.setOrderByClause("birth desc");
        PageInfo pageInfo = userService.selectPageByExample(example, pageNum, pageSize);
        return ResponseUtil.success(pageInfo);
    }
}
