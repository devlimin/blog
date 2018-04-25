package com.limin.blog.mapper;

import com.limin.blog.model.ForumTheme;
import com.limin.blog.model.ForumThemeExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ForumThemeMapper {
    long countByExample(ForumThemeExample example);

    int deleteByExample(ForumThemeExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ForumTheme record);

    int insertSelective(ForumTheme record);

    List<ForumTheme> selectByExample(ForumThemeExample example);

    ForumTheme selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") ForumTheme record, @Param("example") ForumThemeExample example);

    int updateByExample(@Param("record") ForumTheme record, @Param("example") ForumThemeExample example);

    int updateByPrimaryKeySelective(ForumTheme record);

    int updateByPrimaryKey(ForumTheme record);
}