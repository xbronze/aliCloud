package com.xbronze.alicloud.stock.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: xbronze
 * @date: 2024-04-27 16:41
 * @description: TODO
 */
@RestController
@RequestMapping("/stock")
public class StockController {

    @Value("${server.port}")
    String port;

    @GetMapping("/list")
    public String orderList() {
        System.out.println("查询库存列表，服务端口：" + port);
        return "查询库存列表，服务端口：" + port;
    }

}
