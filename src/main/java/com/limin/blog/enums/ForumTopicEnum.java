package com.limin.blog.enums;

public enum ForumTopicEnum {
    DELETED(-1,"删除"),PUBLISHED(0,"发表"),GARBAGE(1,"回收站");

    private Integer val;
    private String msg;
    private ForumTopicEnum(Integer val, String value){
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
