package com.limin.blog.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.limin.blog.enums.MessageEnum;
import com.limin.blog.mapper.MessageMapper;
import com.limin.blog.mapper.UserMapper;
import com.limin.blog.model.Message;
import com.limin.blog.model.MessageExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private SensitiveService sensitiveService;

    public Message add(Message message){
        message.setContent(sensitiveService.filter(message.getContent()));
        messageMapper.insertSelective(message);
        return message;
    }

    public PageInfo<Message> conversations(Integer userId, Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Message> messages =messageMapper.getConversations(userId);
        PageInfo<Message> pageInfo = new PageInfo(messages);
        return pageInfo;
    }

    public PageInfo<Message> conversationDetails(String conversationId, Integer pageNum, Integer pageSize){
        MessageExample example = new MessageExample();
        example.createCriteria().andConversationIdEqualTo(conversationId);
        example.setOrderByClause("release_date desc");
        PageHelper.startPage(pageNum,pageSize);
        List<Message> messages = messageMapper.selectByExample(example);
        PageInfo<Message> pageInfo = new PageInfo<>(messages);
        return pageInfo;
    }

    public long conversationUnreadCount(Integer userId,String conversationId){
        MessageExample example = new MessageExample();
        example.createCriteria().andConversationIdEqualTo(conversationId)
                .andToUserIdEqualTo(userId).andIsReadEqualTo(MessageEnum.UNREAD.getVal());
        long count = messageMapper.countByExample(example);
        return count;
    }
}