package com.limin.blog.service;

import com.limin.blog.exception.BizException;
import com.limin.blog.mapper.AdminUserMapper;
import com.limin.blog.model.AdminUser;
import com.limin.blog.model.AdminUserExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminUserService {
    @Autowired
    private AdminUserMapper adminUserMapper;

    public AdminUser login(String name, String password){
        AdminUserExample adminUserExample = new AdminUserExample();
        adminUserExample.createCriteria().andNameEqualTo(name).andPasswordEqualTo(password);
        List<AdminUser> adminUsers = adminUserMapper.selectByExample(adminUserExample);
        if (adminUsers==null|| adminUsers.size()==0) {
            throw new BizException(2,"用户名或密码错误");
        }
        return adminUsers.get(0);
    }
}
