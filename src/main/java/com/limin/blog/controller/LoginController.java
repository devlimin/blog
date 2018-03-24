package com.limin.blog.controller;

import com.limin.blog.model.User;
import com.limin.blog.model.UserExample;
import com.limin.blog.service.UserService;
import com.limin.blog.util.EmailHelper;
import com.limin.blog.util.EncryptUtil;
import com.limin.blog.util.ResponseUtil;
import com.limin.blog.util.VerifyHelper;
import com.limin.blog.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("account")
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailHelper emailHelper;

    @Autowired
    private VerifyHelper verifyHelper;

    @GetMapping(value = {"","/"})
    public String account(){
        return "user/login";
    }

    @GetMapping(value = "/verifyImg/{type}")
    public void verifyImg(HttpSession session, HttpServletResponse response,@PathVariable(value = "type") String type){
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        //生成随机字串
        String verifyCode = VerifyHelper.generateVerifyCode(4);
        if(type.equals("1")) {
            //删除以前的
            session.removeAttribute("regCode");
            session.setAttribute("regCode", verifyCode.toLowerCase());
        } else if (type.equals("2")) {
            //删除以前的
            session.removeAttribute("loginCode");
            session.setAttribute("loginCode", verifyCode.toLowerCase());
        }
        //生成图片
        int w = 100, h = 30;
        try {
            VerifyHelper.outputImage(w, h, response.getOutputStream(), verifyCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("emailCheck")
    @ResponseBody
    public Response emailCheck(@RequestParam("email") String email) {
        UserExample example =new UserExample();
        example.createCriteria().andEmailEqualTo(email);
        List<User> users = userService.selectByExample(example);
        if (users == null || users.size() >= 1) {
            return ResponseUtil.error(2,"此邮箱已被注册");
        }
        return ResponseUtil.sucess();
    }

    @PostMapping(value = "regist")
    @ResponseBody
    public Response regist(@RequestParam(value = "email") String email,
                           @RequestParam(value = "name") String name,
                           @RequestParam(value = "password") String password,
                           @RequestParam("code") String code,
                           HttpSession session) {
        if(!code.equalsIgnoreCase((String) session.getAttribute("regCode"))){
            return ResponseUtil.error(2,"验证码错误");
        }
        UserExample example =new UserExample();
        example.createCriteria().andEmailEqualTo(email);
        List<User> users = userService.selectByExample(example);
        if (users == null || users.size() >= 1) {
            return ResponseUtil.error(2,"此邮箱已被注册");
        }
        userService.regist(email, name, password);
        return ResponseUtil.sucess();
    }

    @PostMapping(value = "login")
    @ResponseBody
    public Response<User> login(@RequestParam(value = "email") String email,
                                @RequestParam(value = "password") String password,
                                @RequestParam(value = "code") String code,
                                @RequestParam(value = "rememberme", defaultValue = "false") boolean rememberme,
                                HttpSession session,
                                HttpServletResponse response) {
        if(!code.equalsIgnoreCase((String) session.getAttribute("loginCode"))){
            return ResponseUtil.error(2,"验证码错误");
        }
        UserExample userExample = new UserExample();
        userExample.createCriteria().andEmailEqualTo(email);
        List<User> uses = userService.selectByExample(userExample);
        if (uses == null || uses.size() == 0) { //成功
            return ResponseUtil.error(2,"邮箱不正确");
        }
        User user = uses.get(0);
        if (!EncryptUtil.MD5(EncryptUtil.MD5(password + user.getSalt())).equals(user.getPassword())) {
            return ResponseUtil.error(2,"密码不正确");
        }

//        Cookie cookie = new Cookie("token",EncryptUtil.MD5(user.getName()));
//        cookie.setHttpOnly(true);
//        cookie.setPath("/");
//        if(rememberme) {
//            cookie.setMaxAge(60*60*24*30);
//        }
//        response.addCookie(cookie);
        return ResponseUtil.sucess();
    }


}
