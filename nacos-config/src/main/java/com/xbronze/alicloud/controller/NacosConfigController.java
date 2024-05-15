package com.xbronze.alicloud.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: xbronze
 * @date: 2024-05-15 14:29
 * @description: TODO
 */
@RestController
@RefreshScope
@RequestMapping("/nacos/config")
public class NacosConfigController {

    @Value("${user.name}")
    public String userName;

    @GetMapping("/read")
    public String read() {
        return this.userName;
    }

}
