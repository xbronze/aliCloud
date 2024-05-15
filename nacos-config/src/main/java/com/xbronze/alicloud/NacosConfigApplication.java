package com.xbronze.alicloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class NacosConfigApplication
{
    public static void main( String[] args )
    {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(NacosConfigApplication.class, args);
//        String userName = applicationContext.getEnvironment().getProperty("user.name");
//        String userAge = applicationContext.getEnvironment().getProperty("user.age");
//        String currentEnv = applicationContext.getEnvironment().getProperty("current.env");
//        System.out.println("user name :" + userName + ", and user age :" + userAge + ", currentEnv：" + currentEnv);
    }
}
