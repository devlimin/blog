package com.limin.blog.model;

import java.util.Date;

/**
 *
 * This class was generated by MyBatis Generator.
 * This class corresponds to the database table comment
 *
 * @mbg.generated do_not_delete_during_merge Tue Apr 10 20:03:55 CST 2018
 */
public class Comment {
    /**
     * Database Column Remarks:
     *   id
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column comment.id
     *
     * @mbg.generated Tue Apr 10 20:03:55 CST 2018
     */
    private Integer id;

    /**
     * Database Column Remarks:
     *   用户id
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column comment.user_id
     *
     * @mbg.generated Tue Apr 10 20:03:55 CST 2018
     */
    private Integer userId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column comment.to_user_id
     *
     * @mbg.generated Tue Apr 10 20:03:55 CST 2018
     */
    private Integer toUserId;

    /**
     * Database Column Remarks:
     *   评论主体类型
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column comment.entity_type
     *
     * @mbg.generated Tue Apr 10 20:03:55 CST 2018
     */
    private Integer entityType;

    /**
     * Database Column Remarks:
     *   评论主体id
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column comment.entity_id
     *
     * @mbg.generated Tue Apr 10 20:03:55 CST 2018
     */
    private Integer entityId;

    /**
     * Database Column Remarks:
     *   发布时间
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column comment.release_date
     *
     * @mbg.generated Tue Apr 10 20:03:55 CST 2018
     */
    private Date releaseDate;

    /**
     * Database Column Remarks:
     *   状态
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column comment.status
     *
     * @mbg.generated Tue Apr 10 20:03:55 CST 2018
     */
    private Integer status;

    /**
     * Database Column Remarks:
     *   内容
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column comment.content
     *
     * @mbg.generated Tue Apr 10 20:03:55 CST 2018
     */
    private String content;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column comment.id
     *
     * @return the value of comment.id
     *
     * @mbg.generated Tue Apr 10 20:03:55 CST 2018
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column comment.id
     *
     * @param id the value for comment.id
     *
     * @mbg.generated Tue Apr 10 20:03:55 CST 2018
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column comment.user_id
     *
     * @return the value of comment.user_id
     *
     * @mbg.generated Tue Apr 10 20:03:55 CST 2018
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column comment.user_id
     *
     * @param userId the value for comment.user_id
     *
     * @mbg.generated Tue Apr 10 20:03:55 CST 2018
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column comment.to_user_id
     *
     * @return the value of comment.to_user_id
     *
     * @mbg.generated Tue Apr 10 20:03:55 CST 2018
     */
    public Integer getToUserId() {
        return toUserId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column comment.to_user_id
     *
     * @param toUserId the value for comment.to_user_id
     *
     * @mbg.generated Tue Apr 10 20:03:55 CST 2018
     */
    public void setToUserId(Integer toUserId) {
        this.toUserId = toUserId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column comment.entity_type
     *
     * @return the value of comment.entity_type
     *
     * @mbg.generated Tue Apr 10 20:03:55 CST 2018
     */
    public Integer getEntityType() {
        return entityType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column comment.entity_type
     *
     * @param entityType the value for comment.entity_type
     *
     * @mbg.generated Tue Apr 10 20:03:55 CST 2018
     */
    public void setEntityType(Integer entityType) {
        this.entityType = entityType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column comment.entity_id
     *
     * @return the value of comment.entity_id
     *
     * @mbg.generated Tue Apr 10 20:03:55 CST 2018
     */
    public Integer getEntityId() {
        return entityId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column comment.entity_id
     *
     * @param entityId the value for comment.entity_id
     *
     * @mbg.generated Tue Apr 10 20:03:55 CST 2018
     */
    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column comment.release_date
     *
     * @return the value of comment.release_date
     *
     * @mbg.generated Tue Apr 10 20:03:55 CST 2018
     */
    public Date getReleaseDate() {
        return releaseDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column comment.release_date
     *
     * @param releaseDate the value for comment.release_date
     *
     * @mbg.generated Tue Apr 10 20:03:55 CST 2018
     */
    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column comment.status
     *
     * @return the value of comment.status
     *
     * @mbg.generated Tue Apr 10 20:03:55 CST 2018
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column comment.status
     *
     * @param status the value for comment.status
     *
     * @mbg.generated Tue Apr 10 20:03:55 CST 2018
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column comment.content
     *
     * @return the value of comment.content
     *
     * @mbg.generated Tue Apr 10 20:03:55 CST 2018
     */
    public String getContent() {
        return content;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column comment.content
     *
     * @param content the value for comment.content
     *
     * @mbg.generated Tue Apr 10 20:03:55 CST 2018
     */
    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }
}