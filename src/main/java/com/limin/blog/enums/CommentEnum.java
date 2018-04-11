package com.limin.blog.enums;

public enum CommentEnum {
    DELETED(-1,"删除"),PUBLISHED(0,"发表");

    private Integer val;
    private String msg;
    private CommentEnum(Integer val, String msg) {
        this.val = val;
        this.msg = msg;
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
