package com.limin.blog.service;

import com.limin.blog.mapper.SysCategoryMapper;
import com.limin.blog.model.SysCategory;
import com.limin.blog.model.SysCategoryExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysCategoryService {
    @Autowired
    SysCategoryMapper sysCategoryMapper;

    public List<SysCategory> selectAll() {
        SysCategoryExample sysCategoryExample = new SysCategoryExample();
        sysCategoryExample.setOrderByClause("id asc");
        List<SysCategory> sysCategories =  sysCategoryMapper.selectByExample(sysCategoryExample);
        return sysCategories;
    }
}
