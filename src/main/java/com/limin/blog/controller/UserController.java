package com.limin.blog.controller;

import com.limin.blog.constant.BlogConst;
import com.limin.blog.model.User;
import com.limin.blog.service.UserService;
import com.limin.blog.util.ResponseUtil;
import com.limin.blog.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.UUID;

@Controller
@RequestMapping(value = "user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping(value = "man/info")
    public String info(){
        return "user/info";
    }

    @ResponseBody
    @PostMapping(value = "man/updateName")
    public Response updateName(@RequestParam(value = "name")String name,
                               HttpSession session){
        User user= (User) session.getAttribute(BlogConst.LOGIN_SESSION_KEY);
        userService.updateName(user.getId(),name);
        user.setName(name);
        return ResponseUtil.success(name);
    }
    @ResponseBody
    @PostMapping(value = "man/updateMotto")
    public Response updateMotto(@RequestParam(value = "motto")String motto,
                               HttpSession session){
        User user= (User) session.getAttribute(BlogConst.LOGIN_SESSION_KEY);
        userService.updateMotto(user.getId(),motto);
        user.setMotto(motto);
        return ResponseUtil.success(motto);
    }

    String parent = "E:\\develop\\idea\\project\\blog\\src\\main\\resources\\static\\headImg";
    @ResponseBody
    @PostMapping(value = "man/updateHeadImg")
    public Response updateHeadImg(@RequestParam("file")MultipartFile img,
                                  HttpServletRequest request){

        Calendar calendar = Calendar.getInstance();
        String filename = UUID.randomUUID().toString().replaceAll("-","").substring(0,5)+img.getOriginalFilename();
        File file = new File(parent, filename);
        if(!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        try {
            img.transferTo(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        User user = (User) request.getSession().getAttribute(BlogConst.LOGIN_SESSION_KEY);
        user.setHeadUrl("\\user\\"+filename);
        userService.updateHeadUrl(user.getId(),user.getHeadUrl());
        return ResponseUtil.success(user.getHeadUrl());
    }
    @Autowired
    private ResourceLoader resourceLoader;
    @RequestMapping(method = RequestMethod.GET, value = "/{filename:.+}")
    @ResponseBody
    public ResponseEntity<?> getFile(@PathVariable String filename) {
        try {
            String string = Paths.get(parent + File.separator + filename).toString();
            return ResponseEntity.ok(resourceLoader.getResource("file:" + string));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
