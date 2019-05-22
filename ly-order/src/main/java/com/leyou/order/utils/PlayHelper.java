package com.leyou.order.utils;

import com.github.wxpay.sdk.WXPay;

import static com.github.wxpay.sdk.WXPayConstants.*;//静态导入

import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.order.config.PayConfig;
import com.leyou.order.enums.OrderStatusEnum;
import com.leyou.order.enums.PayState;
import com.leyou.order.mapper.OrderMapper;
import com.leyou.order.mapper.OrderStatusMapper;
import com.leyou.order.pojo.Order;
import com.leyou.order.pojo.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class PlayHelper {
    @Autowired
    private WXPay wxPay;
    @Autowired
    private PayConfig config;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderStatusMapper statusMapper;

    public String createOrder(Long orderId, Long totalPay, String dest) {
        try {
            Map<String, String> data = new HashMap<>();
            //商品描述
            data.put("body", dest);
            //订单号
            data.put("out_trade_no", orderId.toString());
            //金额，单位：分
            data.put("total_fee", totalPay.toString());
            //调用微信支付的终端IP
            data.put("spbill_create_ip", "127.0.0.1");
            //回调地址
            data.put("notify_url", config.getNotifyUrl());
            //交易类型为扫码支付
            data.put("trade_type", "NATIVE");
            //利用wxpay工具，完成下单
            Map<String, String> result = wxPay.unifiedOrder(data);
            //判断通信和业务标识
            isSuccess(result);

            //下单成功，获取支付链接
            String url = result.get("code_url");
            return url;
        } catch (Exception e) {
            log.error("[微信下单]创建预交易订单失败", e);
            return null;
        }
    }

    /**
     * 判断通信和业务标识
     *
     * @param result
     */
    public void isSuccess(Map<String, String> result) {
        //判断通信标识
        String returnCode = result.get("return_code");
        if (FAIL.equals(returnCode)) {
            //通信失败
            log.error("[微信下单]微信下单通信失败，失败原因:{}", result.get("return_msg"));
            throw new LyException(ExceptionEnum.WX_PAY_NOTIFY_PARAM_ERROR);
        }
        //判断业务标识
        String resultCode = result.get("result_code");
        if (FAIL.equals(resultCode)) {
            //通信失败
            log.error("[微信下单]微信下单业务失败，失败原因:{}", result.get("err_code_des"));
            throw new LyException(ExceptionEnum.WX_PAY_NOTIFY_PARAM_ERROR);
        }
    }

    /**
     * 校验签名
     *
     * @param data
     */
    public void isValidAign(Map<String, String> data) {
        try {
            //重新生成签名
            String sign1 = WXPayUtil.generateSignature(data, config.getKey(), SignType.HMACSHA256);
            String sign2 = WXPayUtil.generateSignature(data, config.getKey(), SignType.MD5);
            //和传递的签名进行比较
            String sign = data.get("sign");
            if (!StringUtils.equals(sign, sign1) && !StringUtils.equals(sign, sign2)) {
                throw new LyException(ExceptionEnum.SIGN_ERROR);
            }
        } catch (Exception e) {
            throw new LyException(ExceptionEnum.SIGN_ERROR);
        }
    }

    public PayState queryOrderStatus(Long orderId) {
        try {
            //组织请求参数
            Map<String, String> data = new HashMap<>();
            //订单号
            data.put("out_trade_no", orderId.toString());
            //查询订单状态
            Map<String, String> result = wxPay.orderQuery(data);
            //校验状态
            isSuccess(result);
            //校验签名
            isValidAign(result);
            //校验金额
            String totalFeeStr = result.get("total_fee");
            String tradeNo = result.get("out_trade_no");
            if (StringUtils.isEmpty(totalFeeStr) || StringUtils.isEmpty(tradeNo)) {
                throw new LyException(ExceptionEnum.ORDER_PARAM_ERROR);
            }
            //获取结果中的金额
            long totalPay = Long.valueOf(totalFeeStr);
            //获取订单中的金额
            Order order = orderMapper.selectByPrimaryKey(orderId);
            Long actualPay = /*order.getActualPay()*/1L;
            if (totalPay != actualPay) {
                throw new LyException(ExceptionEnum.ORDER_PARAM_ERROR);
            }
            //解析订单状态
            String state = result.get("trade_state");
            if (SUCCESS.equals(state)) {
                //支付成功
                //修改订单状态
                OrderStatus orderStatus = new OrderStatus();
                orderStatus.setStatus(OrderStatusEnum.PAYED.value());
                orderStatus.setOrderId(orderId);
                orderStatus.setPaymentTime(new Date());
                int count = statusMapper.updateByPrimaryKeySelective(orderStatus);
                if (count != 1) {
                    throw new LyException(ExceptionEnum.UPDATE_ORDER_STATUS_ERROR);
                }
                //返回成功
                return PayState.SUCCESS;
            }
            if ("NOTPAY".equals(state) || "USERPAYING".equals(state)) {
                return PayState.NOT_PAY;
            }
            return PayState.FAIL;
        } catch (Exception e) {
            return PayState.NOT_PAY;
        }
    }
}
