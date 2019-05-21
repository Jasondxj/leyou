package com.leyou.item.service.impl;

import com.leyou.common.dto.CartDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@SpringBootTest
public class GoodsServiceImplTest {
    @Autowired
    private GoodsServiceImpl goodsService;

    @Test
    public void desreaseStock() {
        List<CartDto> cartDtos = Arrays.asList(new CartDto(2600242L, 2), new CartDto(2600248L, 2));
        goodsService.desreaseStock(cartDtos);
    }
}