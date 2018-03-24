package com.limin.blog.enums;

public enum CommentEnum {
    DELETED(-1,"删除"),PUBLISHED(0,"发表");

    private Integer key;
    private String msg;
    private CommentEnum(Integer key, String msg) {
        this.key = key;
        this.msg = msg;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
