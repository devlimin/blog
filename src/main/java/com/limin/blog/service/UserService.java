package com.limin.blog.service;

import com.limin.blog.enums.UserEnum;
import com.limin.blog.mapper.UserMapper;
import com.limin.blog.model.User;
import com.limin.blog.model.UserExample;
import com.limin.blog.util.EmailHelper;
import com.limin.blog.util.EncryptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EmailHelper emailHelper;

    public void regist(String email, String name, String password){
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        user.setPassword(EncryptUtil.MD5(EncryptUtil.MD5(password + user.getSalt())));
        String head = String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000));
        user.setHeadUrl(head);
        user.setBirth(new Date());
        user.setState(UserEnum.INITED.getKey());
        userMapper.insert(user);
        emailHelper.sendEmail();
    }

    public User selectById(Integer id) {
        return userMapper.selectByPrimaryKey(id);
    }

    public List<User> selectByExample(UserExample userExample) {
        return userMapper.selectByExample(userExample);
    }
}
