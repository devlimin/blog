package com.limin.blog.vo;

import com.limin.blog.model.Message;
import com.limin.blog.model.User;

public class MessageVo {
    private Message message;
    private User user;
    private long count;

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
