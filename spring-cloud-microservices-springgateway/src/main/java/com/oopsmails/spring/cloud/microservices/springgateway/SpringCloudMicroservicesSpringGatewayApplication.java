package com.oopsmails.spring.cloud.microservices.springgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SpringCloudMicroservicesSpringGatewayApplication {


    public static void main(String[] args) {
        SpringApplication.run(SpringCloudMicroservicesSpringGatewayApplication.class, args);
    }

}
