package com.limin.blog.enums;

public enum EntityEnum {
    SYSTEM(0,"系统"),USER(1,"用户"),ARITCLE(2,"文章"),COMMENT(3,"评论");
    private Integer val;
    private String msg;
    private EntityEnum(Integer val, String msg) {
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
