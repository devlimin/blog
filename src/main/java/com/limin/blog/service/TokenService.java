package com.limin.blog.service;

import com.limin.blog.constant.BlogConst;
import com.limin.blog.mapper.TokenMapper;
import com.limin.blog.model.Token;
import com.limin.blog.model.TokenExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class TokenService {
    @Autowired
    private TokenMapper tokenMapper;

    public String addToken(Integer uid){
        Token token = new Token();
        token.setUserId(uid);
        token.setToken(UUID.randomUUID().toString());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,30);
        Date date = calendar.getTime();
        token.setExpired(date);
        token.setStatus(BlogConst.TOOKEN_VALID);
        tokenMapper.insertSelective(token);
        return token.getToken();
    }
    public void inValid(String t){
        TokenExample example = new TokenExample();
        example.createCriteria().andTokenEqualTo(t);
        List<Token> tokens = tokenMapper.selectByExample(example);
        if (tokens != null && tokens.size()>0) {
            Token token = tokens.get(0);
            token.setStatus(BlogConst.TOOKEN_INVALID);
            tokenMapper.updateByPrimaryKey(token);
        }
    }
}
