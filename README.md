# Spring Alibaba Cloud实践项目

### 项目中框架版本

- SpringBoot: 2.6.13
- SpringCloud: 2021.0.5
- SpringAlibabaCloud: 2021.0.5.0
- Nacos Version: 2.2.0
- Sentinel Version: 1.8.6
- RocketMQ Version: 4.9.4
- Dubbo Version: ~
- Seata Version: 1.6.1

注意事项：
1. `Alibaba Nacos Discovery 2021.0.5.0`版本中，已经不再包含 `spring-cloud-loadbalancer`依赖，需要手动在消费端的pom中添加。
```xml
<!-- https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-loadbalancer -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-loadbalancer</artifactId>
    <version>3.0.5</version>
</dependency>
```
