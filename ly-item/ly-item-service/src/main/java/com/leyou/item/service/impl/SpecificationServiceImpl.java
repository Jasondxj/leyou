package com.leyou.item.service.impl;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.ISpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class SpecificationServiceImpl implements ISpecificationService{
    @Autowired
    private SpecParamMapper specParamMapper;
    @Autowired
    private SpecGroupMapper specGroupMapper;
    @Override
    public List<SpecGroup> queryByCid(Long cid) {
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);
        List<SpecGroup> list = specGroupMapper.select(specGroup);
        if (CollectionUtils.isEmpty(list)){
            throw new LyException(ExceptionEnum.SPEC_GROUP_NOT_FOUND);
        }
        return list;
    }

    @Override
    public List<SpecParam> queryParamByGid(Long gid) {
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        List<SpecParam> list = specParamMapper.select(specParam);
        if (CollectionUtils.isEmpty(list)){
            throw new LyException(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }
        return list;
    }

    @Transactional
    @Override
    public void addGroup(String name,Long cid) {
        SpecGroup specGroup = new SpecGroup();
        specGroup.setName(name);
        specGroup.setCid(cid);
        int count = specGroupMapper.insertSelective(specGroup);
        if (count!=1){
            throw new LyException(ExceptionEnum.GROUP_SAVE_ERROR);
        }
    }

    @Transactional
    @Override
    public void addParam(SpecParam specParam) {
        int count = specParamMapper.insertSelective(specParam);
        if (count!=1){
            throw new LyException(ExceptionEnum.PARAM_SAVE_ERROR);
        }
    }

    @Transactional
    @Override
    public void deleteGroup(Long id) {
        int count = specGroupMapper.deleteByPrimaryKey(id);
        if (count!=1){
            throw new LyException(ExceptionEnum.GROUP_DELETE_ERROR);
        }
    }

    @Override
    public List<SpecParam> queryParamList(Long gid, Long cid, Boolean searching) {
        SpecParam specParam = new SpecParam();
        specParam.setCid(cid);
        specParam.setGroupId(gid);
        specParam.setSearching(searching);
        List<SpecParam> list = specParamMapper.select(specParam);
        if (CollectionUtils.isEmpty(list)){
            throw new LyException(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }
        return list;
    }

}
