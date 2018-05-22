package com.limin.blog.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.limin.blog.enums.SysCategoryEnum;
import com.limin.blog.exception.BizException;
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

    public SysCategory selectById(Integer sysCateId) {
        return sysCategoryMapper.selectByPrimaryKey(sysCateId);
    }

    public PageInfo selectByExample(SysCategoryExample example, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<SysCategory> sysCategories = sysCategoryMapper.selectByExample(example);
        return new PageInfo(sysCategories);
    }

    public void disable(Integer id) {
        SysCategory sysCategory = selectById(id);
        if (sysCategory==null) {
            throw new BizException(2,"没有该系统分类");
        }
        sysCategory.setStatus(SysCategoryEnum.DELETED.getVal());
        sysCategoryMapper.updateByPrimaryKeySelective(sysCategory);
    }

    public void enable(Integer id) {
        SysCategory sysCategory = selectById(id);
        if (sysCategory==null) {
            throw new BizException(2,"没有该系统分类");
        }
        sysCategory.setStatus(SysCategoryEnum.PUBLISHED.getVal());
        sysCategoryMapper.updateByPrimaryKeySelective(sysCategory);
    }

    public SysCategory add(String name) {
        SysCategory sysCategory = new SysCategory();
        sysCategory.setName(name);
        sysCategory.setStatus(SysCategoryEnum.PUBLISHED.getVal());
        sysCategoryMapper.insertSelective(sysCategory);
        return sysCategory;
    }
}
