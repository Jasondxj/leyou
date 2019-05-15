package com.leyou.item.service;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Spu;

import java.util.List;

public interface IGoodsService {
    public PageResult<Spu> querySpuByPage(Integer page, Integer rows, Boolean saleable, String key);
    public void loadCategoryAndBrandName(List<Spu> spus);
}
