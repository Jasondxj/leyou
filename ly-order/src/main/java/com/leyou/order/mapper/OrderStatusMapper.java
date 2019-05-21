package com.leyou.order.mapper;

import com.leyou.order.pojo.OrderStatus;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface OrderStatusMapper extends Mapper<OrderStatus> {
}
