package com.xbronze.alicloud.controller;

import com.xbronze.alicloud.feign.StockFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: xbronze
 * @date: 2024-05-12 14:35
 * @description: TODO
 */
@RestController
@RequestMapping("/order/feign")
public class OrderOpenFeignController {

    @Autowired
    StockFeignService stockFeignService;

    @GetMapping("/stockList")
    public String getOrder() {
        String msg = stockFeignService.orderList();
        return "hello feign : " + msg;
    }
}
