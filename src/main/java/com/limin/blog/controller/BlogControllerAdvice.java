package com.limin.blog.controller;

import com.limin.blog.exception.BizException;
import com.limin.blog.util.ResponseUtil;
import com.limin.blog.vo.Response;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice(basePackages = "com.limin.blog.controller")
public class BlogControllerAdvice {

    @ExceptionHandler(BizException.class)
    @ResponseBody
    public Response handleException(BizException e){
        return ResponseUtil.error(e.getCode(),e.getMsg());
    }
}
