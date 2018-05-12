package com.limin.blog.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller(value = "adminIndex")
@RequestMapping(value = "admin")
public class IndexController {
    @GetMapping(value = {"index",""})
    public String index(){
        return "admin/admin";
    }
}
