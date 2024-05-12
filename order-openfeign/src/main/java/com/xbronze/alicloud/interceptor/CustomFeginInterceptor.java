package com.xbronze.alicloud.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * @author: xbronze
 * @date: 2024-05-12 18:13
 * @description: Fegin拦截器
 */
public class CustomFeginInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        String url = requestTemplate.url();
        System.out.println("CustomFeginInterceptor拦截器，url：" + url);
    }
}
