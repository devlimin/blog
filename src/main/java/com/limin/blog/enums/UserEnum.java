package com.limin.blog.enums;

public enum UserEnum {
    INITED(0,"初始"),ACTIVED(1,"激活");

    private Integer key;
    private String msg;
    private UserEnum(Integer key, String msg) {
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
