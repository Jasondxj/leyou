package com.leyou.item.api;

import com.leyou.common.dto.CartDto;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface GoodsApi {
    /**
     * 减库存
     * @param cartDtos
     * @return
     */
    @PostMapping("stock/decrease")
    public void desreaseStock(@RequestBody List<CartDto> cartDtos);
    /**
     * 根据sku的id集合查询所有的sku
     *
     * @param ids
     * @return
     */
    @GetMapping("sku/list/ids")
    public List<Sku> querySkuByIds(@RequestParam("ids")List<Long> ids);
    /**
     * 根据spu的id查询spu
     * @param id
     * @return
     */
    @GetMapping("spu/{id}")
    Spu querySpuById(@PathVariable("id") Long id);
    /**
     * 根据spuId查询详情
     * @param spuId
     * @return
     */
    @GetMapping("spu/detail/{id}")
    SpuDetail queryDetailById(@PathVariable("id")Long spuId);

    /**
     * 根据SPU查询下面的所有SKU
     * @param spuId
     * @return
     */
    @GetMapping("sku/list")
    List<Sku> querySkuBySpuId(@RequestParam("id")Long spuId);
    /**
     * 分页查询SPU
     *
     * @param page
     * @param rows
     * @param saleable
     * @param key
     * @return
     */
    @GetMapping("spu/page")
    PageResult<Spu> querySpuByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "saleable", required = false) Boolean saleable,
            @RequestParam(value = "key", required = false) String key
    ) ;
}
