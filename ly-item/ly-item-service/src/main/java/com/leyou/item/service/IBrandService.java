package com.leyou.item.service;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Brand;

import java.util.List;

public interface IBrandService {
    public PageResult<Brand> queryBypage(Integer page, Integer rows, String sortBy, Boolean desc, String key);

    public void saveBrand(Brand brand, List<Long> cids);
    public Brand queryById(Long id);

    public List<Brand> queryBrandByCid(Long cid);

    public List<Brand> queryByIds(List<Long> ids);
}
