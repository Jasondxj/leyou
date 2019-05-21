package com.leyou.order.service;

import com.leyou.auth.entity.UserInfo;
import com.leyou.common.dto.CartDto;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.IdWorker;
import com.leyou.item.pojo.Sku;
import com.leyou.order.client.AddressClient;
import com.leyou.order.client.GoodsClient;
import com.leyou.order.dto.AddressDto;
import com.leyou.order.dto.OrderDto;
import com.leyou.order.enums.OrderStatusEnum;
import com.leyou.order.interceptors.UserInterceptor;
import com.leyou.order.mapper.OrderDetailMapper;
import com.leyou.order.mapper.OrderMapper;
import com.leyou.order.mapper.OrderStatusMapper;
import com.leyou.order.pojo.Order;
import com.leyou.order.pojo.OrderDetail;
import com.leyou.order.pojo.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper detailMapper;
    @Autowired
    private OrderStatusMapper statusMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private GoodsClient goodsClient;

    @Transactional
    public Long createOrder(OrderDto orderDto) {
        //1.创建订单
        Order order = new Order();
        //1.1订单编号，基本信息
        long orderId = idWorker.nextId();
        order.setOrderId(orderId);
        order.setCreateTime(new Date());
        order.setPaymentType(orderDto.getPaymentType());
        order.setPostFee(0L);
        //1.2用户信息
        UserInfo user = UserInterceptor.getUser();
        order.setUserId(user.getId());
        order.setBuyerNick(user.getUsername());
        order.setBuyerRate(false);
        //1.3收货人地址
        //获取收货人信息
        AddressDto addr = AddressClient.findById(orderDto.getAddressId());
        order.setReceiver(addr.getName());
        order.setReceiverAddress(addr.getAddress());
        order.setReceiverCity(addr.getCity());
        order.setReceiverDistrict(addr.getDistrict());
        order.setReceiverMobile(addr.getPhone());
        order.setReceiverState(addr.getState());
        order.setReceiverZip(addr.getZipCode());
        //1.4金额
        //把cartDto转为一个map，key是skuid，值是num
        Map<Long, Integer> numMap = orderDto.getCarts().stream()
                .collect(Collectors.toMap(CartDto::getSkuId, CartDto::getNum));
        //获取所有sku的id
        Set<Long> ids = numMap.keySet();
        //根据id查询sku
        List<Sku> skus = goodsClient.querySkuByIds(new ArrayList<>(ids));
        //准备orderDetail集合
        List<OrderDetail> details = new ArrayList<>();
        long totalPay = 0L;
        for (Sku sku : skus) {
            totalPay += sku.getPrice() * numMap.get(sku.getId());
            //封装orderTetail
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setImage(StringUtils.substringBefore(sku.getImages(), ","));
            orderDetail.setNum(numMap.get(sku.getId()));
            orderDetail.setOrderId(orderId);
            orderDetail.setOwnSpec(sku.getOwnSpec());
            orderDetail.setPrice(sku.getPrice());
            orderDetail.setSkuId(sku.getId());
            orderDetail.setTitle(sku.getTitle());
            details.add(orderDetail);
        }
        order.setTotalPay(totalPay);
        //实付金额=总金额+邮费-优惠金额
        order.setActualPay(totalPay + order.getPostFee() - 0);
        //1.5把order写入数据库
        int count = orderMapper.insertSelective(order);
        if (count != 1) {
            log.error("[订单服务]创建订单失败，订单ID:{}", orderId);
            throw new LyException(ExceptionEnum.CREATE_ORDER_ERROR);
        }
        //2.创建订单详情
        count = detailMapper.insertList(details);
        if (count != details.size()) {
            log.error("[订单服务]创建订单失败，订单ID:{}", orderId);
            throw new LyException(ExceptionEnum.CREATE_ORDER_ERROR);
        }
        //3.创建订单状态
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setCreateTime(order.getCreateTime());
        orderStatus.setOrderId(orderId);
        orderStatus.setStatus(OrderStatusEnum.UN_PAY.value());
        count = statusMapper.insertSelective(orderStatus);
        if (count != 1) {
            log.error("[订单服务]创建订单失败，订单ID:{}", orderId);
            throw new LyException(ExceptionEnum.CREATE_ORDER_ERROR);
        }
        //4.减库存
        List<CartDto> cartDtos = orderDto.getCarts();
        goodsClient.desreaseStock(cartDtos);
        return orderId;
    }
}
