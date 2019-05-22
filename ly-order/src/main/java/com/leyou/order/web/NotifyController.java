package com.leyou.order.web;

import com.leyou.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("notify")
public class NotifyController {
    @Autowired
    private OrderService orderService;
    /**
     * 微信支付的成功回调
     * @param result
     * @return
     */
    @PostMapping(value = "wxPay",produces = "application/xml")
    public Map<String,String> hello(@RequestBody Map<String,String> result){
        //处理回调
        orderService.handlerResult(result);
        log.info("[支付回调]接受微信支付回调，结果:{}",result);
        //返回成功
        Map<String,String> myResult=new HashMap<>();
        myResult.put("return_code","SUCCESS");
        myResult.put("return_msg","OK");
        return myResult;
    }
}
