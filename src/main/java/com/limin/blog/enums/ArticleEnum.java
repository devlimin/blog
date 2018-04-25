package com.limin.blog.enums;

public enum ArticleEnum {
    DELETED(-1,"删除"),PUBLISHED(0,"发表"),DRAFT(1,"草稿"),GARBAGE(2,"回收站"),NO_COMMENT(3,"禁止评论");

    private Integer val;
    private String msg;
    private ArticleEnum(Integer val, String value){
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
