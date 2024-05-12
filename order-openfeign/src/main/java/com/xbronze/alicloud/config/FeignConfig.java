package com.xbronze.alicloud.config;


import com.xbronze.alicloud.interceptor.CustomFeginInterceptor;
import feign.Logger;
import feign.Request;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

/**
 * @author: xbronze
 * @date: 2024-05-12 15:01
 * @description: TODO
 *
 * 在配置类上使用@Configuration 会将配置作用于所有的服务提供方（全局配置），如果不使用@Configuration，则之争对某一个服务生效
 */
public class FeignConfig {

    @Bean
    public Logger.Level feignLoggerLevel () {
        return Logger.Level.BASIC;
    }


//    @Bean
//    public Request.Options options() {
//        return new Request.Options(5000, 10000);
//    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new CustomFeginInterceptor();
    }
}
