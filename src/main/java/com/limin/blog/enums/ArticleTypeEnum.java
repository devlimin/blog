package com.limin.blog.enums;

public enum ArticleTypeEnum {
    ORIGINAL(1,"原创"),REPRINTED(2,"转载");

    private Integer val;
    private String msg;
    private ArticleTypeEnum(Integer val, String msg) {
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
