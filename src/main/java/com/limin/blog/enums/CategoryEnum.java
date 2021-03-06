package com.limin.blog.enums;

public enum CategoryEnum {
    DELETED(-1,"删除"),PUBLISHED(0,"可用");

    private Integer val;
    private String msg;
    private CategoryEnum(Integer val, String value){
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
