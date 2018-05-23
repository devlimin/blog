package com.limin.blog.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.limin.blog.enums.MessageEnum;
import com.limin.blog.exception.BizException;
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

    public PageInfo<Message> conversations(Integer type, Integer userId, Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Message> messages =messageMapper.getConversations(userId,type);
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
        example.createCriteria().andConversationIdEqualTo(conversationId);
        long count = messageMapper.countByExample(example);
        return count;
    }

    public PageInfo selectByExample(MessageExample example, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Message> messages = messageMapper.selectByExample(example);
        return new PageInfo(messages);
    }

    public Message selectById(Integer id) {
        return messageMapper.selectByPrimaryKey(id);
    }

    public void updateStatus(Integer msgId, Integer status) {
        Message message = selectById(msgId);
        if (message==null) {
            throw new BizException(2,"没有该消息");
        }
        message.setStatus(status);
        messageMapper.updateByPrimaryKeySelective(message);
    }
}