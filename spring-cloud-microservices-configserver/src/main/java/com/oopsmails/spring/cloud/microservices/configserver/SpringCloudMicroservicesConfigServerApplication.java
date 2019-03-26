package com.oopsmails.spring.cloud.microservices.configserver;

import com.oopsmails.spring.cloud.microservices.configserver.controller.PropertyConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
@EnableConfigurationProperties(PropertyConfiguration.class)
public class SpringCloudMicroservicesConfigServerApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(SpringCloudMicroservicesConfigServerApplication.class).run(args);
	}

}
