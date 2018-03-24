package com.limin.blog.enums;

public enum EntityEnum {
    SYSTEM(0,"系统"),USER(1,"用户"),ARITCLE(2,"文章"),COMMENT(3,"评论");
    private Integer key;
    private String msg;
    private EntityEnum(Integer key, String msg) {
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
