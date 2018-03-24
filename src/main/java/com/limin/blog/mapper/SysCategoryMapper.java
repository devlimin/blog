package com.limin.blog.mapper;

import com.limin.blog.model.SysCategory;
import com.limin.blog.model.SysCategoryExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SysCategoryMapper {
    long countByExample(SysCategoryExample example);

    int deleteByExample(SysCategoryExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(SysCategory record);

    int insertSelective(SysCategory record);

    List<SysCategory> selectByExample(SysCategoryExample example);

    SysCategory selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") SysCategory record, @Param("example") SysCategoryExample example);

    int updateByExample(@Param("record") SysCategory record, @Param("example") SysCategoryExample example);

    int updateByPrimaryKeySelective(SysCategory record);

    int updateByPrimaryKey(SysCategory record);
}