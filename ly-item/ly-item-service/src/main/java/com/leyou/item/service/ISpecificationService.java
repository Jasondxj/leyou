package com.leyou.item.service;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;

import java.util.List;

public interface ISpecificationService {
    public List<SpecGroup> queryByCid(Long cid);

    public List<SpecParam> queryParamByGid(Long gid);

    public void addGroup(String name,Long cid);

    public void addParam(SpecParam specParam);

    public void deleteGroup(Long id);

    public List<SpecParam> queryParamList(Long gid, Long cid, Boolean searching);
}
