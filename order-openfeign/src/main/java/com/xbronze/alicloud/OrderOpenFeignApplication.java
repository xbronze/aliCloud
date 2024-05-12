package com.xbronze.alicloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableFeignClients
public class OrderOpenFeignApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(OrderOpenFeignApplication.class, args);
    }
}
