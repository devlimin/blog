package com.limin.blog.enums;

public enum FollowEnum {
    FORBIDDEN(2,"禁止"),UNFOLLOW(0,"未关注"),FOLLOW(1,"关注");

    private Integer val;
    private String msg;
    private FollowEnum(Integer val, String value){
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
