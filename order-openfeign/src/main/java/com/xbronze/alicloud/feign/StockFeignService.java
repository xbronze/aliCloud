package com.xbronze.alicloud.feign;

import com.xbronze.alicloud.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author: xbronze
 * @date: 2024-05-12 14:32
 * @description: TODO
 */
@FeignClient(name = "stock-service", path = "/stock", configuration = FeignConfig.class)
public interface StockFeignService {

    @RequestMapping("/list")
    String orderList();
}
