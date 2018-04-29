package com.oopsmails.spring.cloud.microservices.eurekaserver;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class SpringCloudMicroservicesEurekaApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(SpringCloudMicroservicesEurekaApplication.class).run(args);
	}

}
