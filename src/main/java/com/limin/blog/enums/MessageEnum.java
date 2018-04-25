package com.limin.blog.enums;

public enum MessageEnum {
    DELETED(-1,"删除"),PUBLISHED(0,"发表"),READED(1,"已读"),UNREAD(2,"未读");

    private Integer val;
    private String msg;
    private MessageEnum(Integer val, String value){
        this.val = val;
        this.msg = value;
    }

    public Integer getVal() {
        return val;
    }

    public void setVal(Integer val) {
        this.val = val;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
