package com.leyou.item.service;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;

import java.util.List;

public interface IGoodsService {
    public PageResult<Spu> querySpuByPage(Integer page, Integer rows, Boolean saleable, String key);

    public void loadCategoryAndBrandName(List<Spu> spus);

    public void savaGood(Spu spu);

    public SpuDetail queryDetailById(Long spuId);

    public List<Sku> querySkuBySpuId(Long spuId);

    public void updateGood(Spu spu);
}
