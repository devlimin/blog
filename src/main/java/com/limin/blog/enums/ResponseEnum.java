package com.limin.blog.enums;

public enum ResponseEnum {
    SUCCESS(1,"success"),NO_LOGIN(2,"");
    private Integer val;
    private String msg;
    private ResponseEnum(Integer val, String value){
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
