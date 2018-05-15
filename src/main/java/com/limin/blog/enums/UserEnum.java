package com.limin.blog.enums;

public enum UserEnum {
    INITED(0,"初始"),ACTIVED(1,"激活"),DISABLE(2,"禁用");

    private Integer val;
    private String msg;
    private UserEnum(Integer val, String msg) {
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
