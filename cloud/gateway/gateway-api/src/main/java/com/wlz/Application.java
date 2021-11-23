package com.wlz;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.springboot.cloud.auth.client")
@EnableCircuitBreaker
@EnableMethodCache(basePackages = "com.springboot.cloud")
@EnableCreateCacheAnnotation
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}