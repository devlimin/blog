package com.limin.blog.mapper;

import com.limin.blog.model.ForumTopic;
import com.limin.blog.model.ForumTopicExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ForumTopicMapper {
    long countByExample(ForumTopicExample example);

    int deleteByExample(ForumTopicExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ForumTopic record);

    int insertSelective(ForumTopic record);

    List<ForumTopic> selectByExample(ForumTopicExample example);

    ForumTopic selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") ForumTopic record, @Param("example") ForumTopicExample example);

    int updateByExample(@Param("record") ForumTopic record, @Param("example") ForumTopicExample example);

    int updateByPrimaryKeySelective(ForumTopic record);

    int updateByPrimaryKey(ForumTopic record);
}