package com.limin.blog.mapper;

import com.limin.blog.model.Comment;
import com.limin.blog.model.CommentExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CommentMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table comment
     *
     * @mbg.generated Tue Apr 10 20:03:55 CST 2018
     */
    long countByExample(CommentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table comment
     *
     * @mbg.generated Tue Apr 10 20:03:55 CST 2018
     */
    int deleteByExample(CommentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table comment
     *
     * @mbg.generated Tue Apr 10 20:03:55 CST 2018
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table comment
     *
     * @mbg.generated Tue Apr 10 20:03:55 CST 2018
     */
    int insert(Comment record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table comment
     *
     * @mbg.generated Tue Apr 10 20:03:55 CST 2018
     */
    int insertSelective(Comment record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table comment
     *
     * @mbg.generated Tue Apr 10 20:03:55 CST 2018
     */
    List<Comment> selectByExampleWithBLOBs(CommentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table comment
     *
     * @mbg.generated Tue Apr 10 20:03:55 CST 2018
     */
    List<Comment> selectByExample(CommentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table comment
     *
     * @mbg.generated Tue Apr 10 20:03:55 CST 2018
     */
    Comment selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table comment
     *
     * @mbg.generated Tue Apr 10 20:03:55 CST 2018
     */
    int updateByExampleSelective(@Param("record") Comment record, @Param("example") CommentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table comment
     *
     * @mbg.generated Tue Apr 10 20:03:55 CST 2018
     */
    int updateByExampleWithBLOBs(@Param("record") Comment record, @Param("example") CommentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table comment
     *
     * @mbg.generated Tue Apr 10 20:03:55 CST 2018
     */
    int updateByExample(@Param("record") Comment record, @Param("example") CommentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table comment
     *
     * @mbg.generated Tue Apr 10 20:03:55 CST 2018
     */
    int updateByPrimaryKeySelective(Comment record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table comment
     *
     * @mbg.generated Tue Apr 10 20:03:55 CST 2018
     */
    int updateByPrimaryKeyWithBLOBs(Comment record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table comment
     *
     * @mbg.generated Tue Apr 10 20:03:55 CST 2018
     */
    int updateByPrimaryKey(Comment record);
}