package com.leyou.service;

import com.leyou.auth.entity.UserInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.interceptors.UserInterceptor;
import com.leyou.pojo.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.file.OpenOption;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    private static final String KEY_PREFIX = "cart:user:id:";

    public void addCart(Cart cart) {
        //获取用户信息
        UserInfo user = UserInterceptor.getUser();
        String key = KEY_PREFIX + user.getId();
        //hashKey
        String hashKey = cart.getSkuId().toString();
        //记录num
        Integer num = cart.getNum();
        BoundHashOperations<String, Object, Object> operation = redisTemplate.boundHashOps(key);
        //判断当前商品，购物车是否存在
        if (operation.hasKey(hashKey)) {
            //是，添加数量
            String json = operation.get(hashKey).toString();
            cart = JsonUtils.toBean(json, Cart.class);
            cart.setNum(cart.getNum() + num);
        }
        //写回redis
        operation.put(hashKey, JsonUtils.toString(cart));
    }

    public List<Cart> queryCartList() {
        //获取用户信息
        UserInfo user = UserInterceptor.getUser();
        String key = KEY_PREFIX + user.getId();
        if (!redisTemplate.hasKey(key)) {
            throw new LyException(ExceptionEnum.CART_NOT_FOUND);
        }
        BoundHashOperations<String, Object, Object> operation = redisTemplate.boundHashOps(key);
        List<Cart> list = operation.values().stream().map(O -> JsonUtils.toBean(O.toString(), Cart.class)).collect(Collectors.toList());
        return list;
    }

    public void updateCartNum(Long skuId, Integer num) {
        //获取用户信息
        UserInfo user = UserInterceptor.getUser();
        String key = KEY_PREFIX + user.getId();
        BoundHashOperations<String, Object, Object> operation = redisTemplate.boundHashOps(key);
        if (!operation.hasKey(skuId.toString())) {
            throw new LyException(ExceptionEnum.CART_NOT_FOUND);
        }
        Cart cart = JsonUtils.toBean(operation.get(skuId.toString()).toString(), Cart.class);
        cart.setNum(num);
        //写回redis
        operation.put(skuId.toString(), JsonUtils.toString(cart));
    }

    public void deleteCart(Long skuId) {
        //获取用户信息
        UserInfo user = UserInterceptor.getUser();
        String key = KEY_PREFIX + user.getId();
        redisTemplate.opsForHash().delete(key,skuId.toString());
    }
}
