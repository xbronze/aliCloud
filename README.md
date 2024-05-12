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

### Ribbon负载均衡策略

- 线性轮询策略（RoundRobinRule）：这是Ribbon的默认负载均衡策略，通过一个计数器来循环选择服务实例，每次循环计数器都会加1，如果连续10次都没有取到服务，则会报“No available alive servers after 10 tries from loadbalancer.”警告。
- 重试策略（RetryRule）：如果选择到的服务实例为null或失效，则choose()方法会在失效时间前不断地进行重试，直到找到可用的服务实例或超过失效时间。
- 加权响应时间策略（WeightedResponseTimeRule）：根据每一个服务实例的运行情况先计算出该服务实例的一个权重，然后根据权重进行服务实例的挑选，以调用到更优的服务实例。
- 随机策略（RandomRule）：在所有服务实例中随机找一个服务的索引号，然后从上线的服务中获取对应的服务。
- 客户端配置启动线性轮询策略（ClientConfigEnabledRoundRobinRule）：默认通过线性轮询策略选取服务，通过继承该类并重写choose方法，可以实现更多的策略。
- 最空闲策略（BestAvailableRule）：从所有没有断开的服务中，选取到目前为止请求数量最小的服务。
- 过滤性线性轮询策略（PredicateBasedRule）：提供一个choose方法的模板，通过调用AbstractServerPredicate实现类的过滤方法来过滤出目标的服务，再通过轮询方法选出一个服务。
- 区域感知轮询策略（ZoneAvoidanceRule）：本身没有重写choose方法，用的还是抽象父类PredicateBasedRule的choose。
- 可用性过滤策略（AvailabilityFilteringRule）：过滤掉由于多次访问失败而处于断路器跳闸状态的服务，以及并发的连接数超过阈值的服务，然后对剩余的服务列表进行线性轮询。


### 注意事项：
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

## 3 Feign

在服务调用业务模块，添加依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

feign功能也需要依赖loadbalancer

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-loadbalancer</artifactId>
</dependency>
```

并且要在配置文件中，停用ribbon负载均衡

```yml
spring:
  cloud:
    loadbalancer:
      ribbon:
        enable: false
```

### 3.1 Feign日志配置

- NONE(默认)：不记录任何日志【性能最佳，适用于生产环境】
- BASIC：仅记录请求方法、URL、响应状态代码以及执行时间【适用于生产环境追踪问题】
- HEADERS：记录BASIC级别的基础上，记录请求和响应的header
- FULL：记录请求和响应的header、body和元数据【比较适用于开发及测试环境定位问题】

### 3.2 契约配置

待补充...



## 4 Nacos Config

在 Nacos Config Starter 中，dataId 的拼接格式如下

`${prefix}-${spring.profiles.active}.${file-extension}`

- prefix 默认为 spring.application.name 的值，也可以通过配置项 spring.cloud.nacos.config.prefix来配置。
- spring.profiles.active 即为当前环境对应的 profile，详情可以参考 Spring Boot文档
- file-extension 为配置内容的数据格式，可以通过配置项 spring.cloud.nacos.config.file-extension来配置。目前只支持 properties 和 yaml 类型。

> 注意，当 activeprofile 为空时，对应的连接符 - 也将不存在，dataId 的拼接格式变成 {prefix}.{file-extension}
