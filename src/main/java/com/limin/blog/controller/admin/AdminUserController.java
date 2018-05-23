package com.limin.blog.controller.admin;

import com.limin.blog.constant.BlogConst;
import com.limin.blog.model.AdminUser;
import com.limin.blog.service.AdminUserService;
import com.limin.blog.util.ResponseUtil;
import com.limin.blog.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(value = "admin")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    @RequestMapping(value = {"account"})
    public String index(){
        return "admin/login";
    }

    @ResponseBody
    @RequestMapping(value = "login")
    public Response login(@RequestParam(value = "name")String name,
                          @RequestParam(value = "password")String password,
                          HttpSession session){
        AdminUser login = adminUserService.login(name, password);
        session.setAttribute(BlogConst.LOGIN_ADMIN_KEY,login);
        return ResponseUtil.success();
    }

    @RequestMapping(value = "logout")
    public String logout(HttpSession session){
        session.removeAttribute(BlogConst.LOGIN_ADMIN_KEY);
        return "admin/login";
    }
}
