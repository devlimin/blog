package com.limin.blog.mapper;

import com.limin.blog.model.ForumReply;
import com.limin.blog.model.ForumReplyExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ForumReplyMapper {
    long countByExample(ForumReplyExample example);

    int deleteByExample(ForumReplyExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ForumReply record);

    int insertSelective(ForumReply record);

    List<ForumReply> selectByExample(ForumReplyExample example);

    ForumReply selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") ForumReply record, @Param("example") ForumReplyExample example);

    int updateByExample(@Param("record") ForumReply record, @Param("example") ForumReplyExample example);

    int updateByPrimaryKeySelective(ForumReply record);

    int updateByPrimaryKey(ForumReply record);
}