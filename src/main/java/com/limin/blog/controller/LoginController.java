package com.limin.blog.controller;

import com.limin.blog.constant.BlogConst;
import com.limin.blog.model.User;
import com.limin.blog.service.TokenService;
import com.limin.blog.service.UserService;
import com.limin.blog.util.ResponseUtil;
import com.limin.blog.util.VerifyHelper;
import com.limin.blog.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
@RequestMapping("account")
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @GetMapping(value = {"","/"})
    public ModelAndView account(@RequestParam(value = "action",defaultValue = "login")String action){
        ModelAndView mv = new ModelAndView("user/login");
        mv.addObject("action",action);
        return mv;
    }

    /**
     * 邮箱唯一检验
     * @param email 邮箱
     * @return
     */
    @GetMapping("emailCheck")
    @ResponseBody
    public Response emailCheck(@RequestParam("email") String email) {
        userService.emailCheck(email);
        return ResponseUtil.success();
    }

    /**
     * 登陆
     * @param email 邮箱
     * @param password 密码
     * @param code 校验码
     * @param session
     * @return
     */
    @PostMapping(value = "regist")
    @ResponseBody
    public Response regist(@RequestParam(value = "email") String email,
                           @RequestParam(value = "password") String password,
                           @RequestParam("code") String code,
                           HttpSession session) {
        if(!code.equalsIgnoreCase((String) session.getAttribute("regCode"))){
            return ResponseUtil.error(2,"验证码错误");
        }
        User user = userService.regist(email, password);
        return ResponseUtil.success();
    }

    @GetMapping(value = "reginfo")
    public ModelAndView info(){
        ModelAndView mv = new ModelAndView("info");
        mv.addObject("msg","我们给您发送了一封激活邮件，请查收。<br>" +
                                                            "若没收到，请重新注册。");
        return mv;
    }

    /**
     * 登陆
     * @param email 邮箱
     * @param password 密码
     * @param code 校验码
     * @param rememberme 记住我
     * @param session
     * @param response
     * @return
     */
    @PostMapping(value = "login")
    @ResponseBody
    public Response<User> login(@RequestParam(value = "email") String email,
                                @RequestParam(value = "password") String password,
                                @RequestParam(value = "code") String code,
                                @RequestParam(value = "rememberme", defaultValue = "false") boolean rememberme,
                                @RequestParam(value = "next",required = false) String next,
                                HttpSession session,
                                HttpServletResponse response) {
        if(!code.equalsIgnoreCase((String) session.getAttribute("loginCode"))){
            return ResponseUtil.error(2,"验证码错误");
        }
        User user = userService.login(email, password);
        session.setAttribute(BlogConst.LOGIN_SESSION_KEY,user);
        if (rememberme) {
            String token = tokenService.addToken(user.getId());
            Cookie cookie = new Cookie(BlogConst.USER_COOKIE_KEY,token);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(3600*24);
            response.addCookie(cookie);
        }
        return ResponseUtil.success(next);
    }

    /**
     * 退出
     * @param token
     * @param session
     * @return
     */
    @GetMapping(value = "/logout")
    public String logout(@CookieValue(value = BlogConst.USER_COOKIE_KEY)String token, HttpSession session, HttpServletRequest request,HttpServletResponse response) {
        session.removeAttribute(BlogConst.LOGIN_SESSION_KEY);
        tokenService.inValid(token);
        Cookie[] cookies = request.getCookies();
        if(cookies !=null&&cookies.length>0) {
            for(Cookie cookie: cookies){
                if (cookie.getName().equals(BlogConst.USER_COOKIE_KEY)){
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                    break;
                }
            }
        }
        return "redirect:/";
    }

    /**
     * 注册验证码
     * @param session
     * @param response
     */
    @GetMapping(value = "/verifyImg/regist")
    public void verifyImgRegist(HttpSession session, HttpServletResponse response){
        //生成随机字串
        String verifyCode = VerifyHelper.generateVerifyCode(4);
        session.removeAttribute("regCode");
        session.setAttribute("regCode", verifyCode.toLowerCase());
        verifyImg(response,verifyCode);
    }

    /**
     * 登陆验证码
     * @param session
     * @param response
     */
    @GetMapping(value = "/verifyImg/login")
    public void verifyImgLogin(HttpSession session, HttpServletResponse response){
        //生成随机字串
        String verifyCode = VerifyHelper.generateVerifyCode(4);
        session.removeAttribute("loginCode");
        session.setAttribute("loginCode", verifyCode.toLowerCase());
        verifyImg(response,verifyCode);
    }

    /**
     * 生成验证图片
     * @param response
     * @param verifyCode
     */
    public void verifyImg(HttpServletResponse response,String verifyCode){
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        //生成图片
        int w = 100, h = 30;
        try {
            VerifyHelper.outputImage(w, h, response.getOutputStream(), verifyCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
