package com.limin.blog.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
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
        try {
            final String content = "恭喜您注册成功，请点击下面的超链接激活账号<br />"
                + "<a href='http://" + InetAddress.getLocalHost().getHostAddress()
                    + ":8080/user/active?id="+user.getId()+"&code=" + EncryptUtil.MD5(user.getName()) + "'>"
                    + "http://" + InetAddress.getLocalHost().getHostAddress()
                    + ":8080/user/active?id="+user.getId()+"&code=" + EncryptUtil.MD5(user.getName())
                    + "</a>";
            new Thread(new Runnable() {
                @Override
                public void run() {
                    emailHelper.sendEmail(user.getEmail(),"账号激活邮件",content);
                }
            }).start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
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
        if (user.getState().equals(UserEnum.INITED.getVal())){
            throw new BizException(2,"该邮箱还未激活");
        }
        if (!EncryptUtil.MD5(EncryptUtil.MD5(password + user.getSalt())).equals(user.getPassword())) {
            throw new BizException(2,"密码不正确");
        }
        return user;
    }

    public boolean active(User user){
        user.setState(UserEnum.ACTIVED.getVal());
        userMapper.updateByPrimaryKeySelective(user);
        return true;
    }

    /**
     * 邮箱唯一检查
     * @param email 邮箱
     * @return 是否唯一
     */
    public boolean emailCheck(String email) {
        UserExample example =new UserExample();
        example.createCriteria().andEmailEqualTo(email).andStateEqualTo(UserEnum.ACTIVED.getVal());
        List<User> users = userMapper.selectByExample(example);
        if (users == null || users.size() >= 1) {
            throw new BizException(2,"此邮箱已被激活");
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
    @CacheEvict(key = "'user:'+#id")
    public void updatePass(User user, String newpass) {
        user.setPassword(EncryptUtil.MD5(EncryptUtil.MD5(newpass+user.getSalt())));
        userMapper.updateByPrimaryKeySelective(user);
    }

    public PageInfo selectPageByExample(UserExample example, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<User> users = userMapper.selectByExample(example);
        return new PageInfo(users);
    }
}
