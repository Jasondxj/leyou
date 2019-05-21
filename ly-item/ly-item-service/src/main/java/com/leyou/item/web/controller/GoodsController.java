package com.leyou.item.web.controller;

import com.leyou.common.dto.CartDto;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.service.IGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GoodsController {
    @Autowired
    private IGoodsService goodsService;

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
    public ResponseEntity<PageResult<Spu>> querySpuByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "saleable", required = false) Boolean saleable,
            @RequestParam(value = "key", required = false) String key
    ) {
        return ResponseEntity.ok(goodsService.querySpuByPage(page, rows, saleable, key));
    }

    /**
     * 新增商品
     *
     * @param spu
     * @return
     */
    @PostMapping("goods")
    public ResponseEntity<Void> savaGood(@RequestBody Spu spu) {
        goodsService.savaGood(spu);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 修改商品
     *
     * @param spu
     * @return
     */
    @PutMapping("goods")
    public ResponseEntity<Void> updateGood(@RequestBody Spu spu) {
        goodsService.updateGood(spu);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 根据spuId查询详情
     *
     * @param spuId
     * @return
     */
    @GetMapping("spu/detail/{id}")
    public ResponseEntity<SpuDetail> queryDetailById(@PathVariable("id") Long spuId) {
        return ResponseEntity.ok(goodsService.queryDetailById(spuId));
    }

    /**
     * 根据SPU查询下面的所有SKU
     *
     * @param spuId
     * @return
     */
    @GetMapping("sku/list")
    public ResponseEntity<List<Sku>> querySkuBySpuId(@RequestParam("id") Long spuId) {
        return ResponseEntity.ok(goodsService.querySkuBySpuId(spuId));
    }
    /**
     * 根据sku的id集合查询所有的sku
     *
     * @param ids
     * @return
     */
    @GetMapping("sku/list/ids")
    public ResponseEntity<List<Sku>> querySkuByIds(@RequestParam("ids")List<Long> ids) {
        return ResponseEntity.ok(goodsService.querySkuByIds(ids));
    }

    /**
     * 根据spu的id查询spu
     *
     * @param id
     * @return
     */
    @GetMapping("spu/{id}")
    public ResponseEntity<Spu> querySpuById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(goodsService.querySpuById(id));
    }

    /**
     * 减库存
     * @param cartDtos
     * @return
     */
    @PostMapping("stock/decrease")
    public ResponseEntity<Void> desreaseStock(@RequestBody List<CartDto> cartDtos){
        goodsService.desreaseStock(cartDtos);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
