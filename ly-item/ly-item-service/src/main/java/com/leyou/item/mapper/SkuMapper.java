package com.leyou.item.mapper;

import com.leyou.item.pojo.Sku;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;
import tk.mybatis.mapper.common.Mapper;
@Repository
public interface SkuMapper extends Mapper<Sku>{
}