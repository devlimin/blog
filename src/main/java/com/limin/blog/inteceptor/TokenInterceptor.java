package com.limin.blog.inteceptor;

import com.limin.blog.constant.BlogConst;
import com.limin.blog.mapper.TokenMapper;
import com.limin.blog.model.Token;
import com.limin.blog.model.TokenExample;
import com.limin.blog.model.User;
import com.limin.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    TokenMapper tokenMapper;

    @Autowired
    UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        User user = (User) httpServletRequest.getSession().getAttribute(BlogConst.LOGIN_SESSION_KEY);
        if(user!= null) {
            return true;
        } else {
            Cookie[] cookies = httpServletRequest.getCookies();
            if (cookies == null || cookies.length==0) {
                return true;
            }
            for(Cookie cookie : cookies){
                //用户cookie
                if (cookie.getName().equals(BlogConst.USER_COOKIE_KEY)){
                    String t = cookie.getValue();
                    TokenExample example = new TokenExample();
                    example.createCriteria().andTokenEqualTo(t);
                    List<Token> tokens = tokenMapper.selectByExample(example);
                    if(tokens!=null&&tokens.size()>0){
                        Token token = tokens.get(0);
                        //是否过期，状态是否有效
                        if (token.getStatus()==0&&token.getExpired().after(new Date())){
                            User login_user = userService.selectById(token.getUserId());
                            httpServletRequest.getSession().setAttribute(BlogConst.LOGIN_SESSION_KEY,login_user);
                        }
                    }
                    return true;
                }
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
