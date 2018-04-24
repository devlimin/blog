package com.limin.blog.service;

import com.limin.blog.enums.UserEnum;
import com.limin.blog.exception.BizException;
import com.limin.blog.mapper.UserMapper;
import com.limin.blog.model.User;
import com.limin.blog.model.UserExample;
import com.limin.blog.util.EmailHelper;
import com.limin.blog.util.EncryptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@CacheConfig(cacheNames = "user")
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EmailHelper emailHelper;

    /**
     * 注册
     * @param email 邮箱
     * @param password 密码
     * @return
     */
    public User regist(String email, String password){
        //邮箱唯一检查
        emailCheck(email);
        User user = new User();
        user.setEmail(email);
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        //密码加盐加密
        user.setPassword(EncryptUtil.MD5(EncryptUtil.MD5(password + user.getSalt())));
        user.setName("u-"+UUID.randomUUID().toString().replaceAll("-","").substring(0,7));
        String head = String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000));
        user.setHeadUrl(head);
        user.setBirth(new Date());
        user.setState(UserEnum.INITED.getVal());
        userMapper.insert(user);
        //发送激活邮件
        emailHelper.sendEmail();
        return user;
    }

    /**
     * 登陆
     * @param email 邮箱
     * @param password 密码
     */
    public User login(String email, String password) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andEmailEqualTo(email);
        List<User> uses = userMapper.selectByExample(userExample);
        if (uses == null || uses.size() == 0) { //成功
            throw new BizException(2,"邮箱不正确");
        }
        User user = uses.get(0);
        if (!EncryptUtil.MD5(EncryptUtil.MD5(password + user.getSalt())).equals(user.getPassword())) {
            throw new BizException(2,"密码不正确");
        }
        return user;
    }
    /**
     * 邮箱唯一检查
     * @param email 邮箱
     * @return 是否唯一
     */
    public boolean emailCheck(String email) {
        UserExample example =new UserExample();
        example.createCriteria().andEmailEqualTo(email);
        List<User> users = userMapper.selectByExample(example);
        if (users == null || users.size() >= 1) {
            throw new BizException(2,"此邮箱已被注册");
        }
        return true;
    }

    public List<User> selectByExample(UserExample userExample) {
        return userMapper.selectByExample(userExample);
    }

    @Cacheable(key = "'user:'+#id")
    public User selectById(Integer id) {
        return userMapper.selectByPrimaryKey(id);
    }

    @CacheEvict(key = "'user:'+#id")
    public void updateName(Integer id, String name) {
        User user= new User();
        user.setId(id);
        user.setName(name);
        userMapper.updateByPrimaryKeySelective(user);
    }
    @CacheEvict(key = "'user:'+#id")
    public void updateMotto(Integer id, String motto) {
        User user= new User();
        user.setId(id);
        user.setMotto(motto);
        userMapper.updateByPrimaryKeySelective(user);
    }
    @CacheEvict(key = "'user:'+#id")
    public void updateHeadUrl(Integer id, String headUrl) {
        User user= new User();
        user.setId(id);
        user.setHeadUrl(headUrl);
        userMapper.updateByPrimaryKeySelective(user);
    }
}
