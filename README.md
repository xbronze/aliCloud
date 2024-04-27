# Spring Alibaba Cloud实践项目

## 1 项目中框架版本

- SpringBoot: 2.6.13
- SpringCloud: 2021.0.5
- SpringAlibabaCloud: 2021.0.5.0
- Nacos Version: 2.2.0
- Sentinel Version: 1.8.6
- RocketMQ Version: 4.9.4
- Dubbo Version: ~
- Seata Version: 1.6.1

## 2 负载均衡LoadBanlancer

### 2.1 随机负载均衡策略

```java
@Configurable
public class RandomLoadBalancerConfig {

    @Bean
    public ReactorLoadBalancer<ServiceInstance> randomLoadBalancer(Environment environment, LoadBalancerClientFactory loadBalancerClientFactory) {
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        return new RandomLoadBalancer(loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class), name);
    }
}
```

### 2.2 设置局部的负载均衡策略

```java
@RestController
@LoadBalancerClient(name = "stock-service", configuration = RandomLoadBalancerConfig.class)
@RequestMapping("/order")
public class OrderController {

    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/say")
    public String sayHello() {
        System.out.println("hello world");
        return "hello world";
    }

    @GetMapping("/stockList")
    public String getOrder() {
        String message = restTemplate.getForObject("http://stock-service/stock/list", String.class);
        return "order " + message;
    }
}
```

### 2.3 设置全局的负载均衡策略：（在启动类上加 @LoadBalancerClients 注解）
```java
@SpringBootApplication
@EnableFeignClients  // 开启 OpenFeign
// 设置全局的负载均衡策略
@LoadBalancerClients(defaultConfiguration = RandomLoadBalancerConfig.class)
public class ConsumerApplication {
 
    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }
 
}
```

### 2.4 Nacos权重负载均衡器

Nacos 中有两种负载均衡策略：权重负载均衡策略和 CMDB（地域就近访问）标签负载均衡策略。它默认的策略是权重。

```java
@Configurable
public class NacosLoadBalancerConfig {

    @Resource
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    @Bean
    public ReactorLoadBalancer<ServiceInstance> nacosLoadBalancer(Environment environment, LoadBalancerClientFactory loadBalancerClientFactory) {
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        return new NacosLoadBalancer(loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class), name, nacosDiscoveryProperties);
    }

}
```

其实对于NacosLoadBalancer还有一个更加简单配置，如下只有设置为true就会使用NacosLoadBalancer策略，当然这种方式没有上面设置的优先级高。

```
spring:
  application:
    name: order-service
  cloud:
    loadbalancer:
      nacos:
        enabled: true
```

# 注意事项：
1. `Alibaba Nacos Discovery 2021.0.5.0`版本中，已经不再包含 `spring-cloud-loadbalancer`依赖，需要手动在消费端的pom中添加。
```xml
<!-- https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-loadbalancer -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-loadbalancer</artifactId>
    <version>3.0.5</version>
</dependency>
```

2. 在`Spring Cloud 2020.0.0`以后就没有在默认使用Ribbon作为负载均衡器了，而且在Cloud官网中也推荐使用LoadBnancer作为负载均衡器，他实现了轮询和随机两种方式（RandomLoadBalancer、RoundRobinLoadBalancer），如果引入了NacosDiscovery的话里面还可以使用NacosLoadBnancer的方式。
> 创建随机负载均衡策略(这些写法都是相通的，可以仿照源码中的轮询策略的关键代码)：
> 可以去源码中的LoadBalancerClientConfiguration中去定位到 reactorServiceInstanceLoadBalancer 方法，然后复制下来，修改几个关键地方即可。

