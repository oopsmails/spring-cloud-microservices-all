package com.oopsmails.spring.cloud.microservices.configserver;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class SpringCloudMicroservicesConfigServerApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(SpringCloudMicroservicesConfigServerApplication.class).run(args);
	}

}
