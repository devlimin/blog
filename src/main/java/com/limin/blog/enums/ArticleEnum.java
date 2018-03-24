package com.limin.blog.enums;

public enum ArticleEnum {
    DELETED(-1,"删除"),PUBLISHED(0,"发表"),DRAFT(1,"草稿"),GARBAGE(2,"垃圾箱"),NO_COMMENT(3,"禁止评论");

    private Integer key;
    private String msg;
    private ArticleEnum(Integer key, String value){
        this.key = key;
        this.msg = value;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public String getValue() {
        return msg;
    }

    public void setValue(String value) {
        this.msg = value;
    }
}
