package com.oopsmails.spring.cloud.microservices.zuulproxy.api;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

@Configuration
public class ProxyApi {

	private static final Set<String> SWAGGER_EXCLUDING_LIST = new HashSet<>();

	static {
		SWAGGER_EXCLUDING_LIST.add("auth-server");
	}

	@Autowired
	ZuulProperties properties;

	@Primary
	@Bean
	public SwaggerResourcesProvider swaggerResourcesProvider() {

		return () -> {
			List<SwaggerResource> resources = new ArrayList<>();
			properties.getRoutes()
					.values()
					.stream()
					.filter(route -> !SWAGGER_EXCLUDING_LIST.contains(route.getServiceId()))
					.forEach(route -> resources.add(createResource(route.getServiceId(), route.getId(), "2.0")));
			return resources;
		};
	}

	private SwaggerResource createResource(String name, String location, String version) {
		SwaggerResource swaggerResource = new SwaggerResource();
		swaggerResource.setName(name);
		swaggerResource.setLocation("/" + location + "/v2/api-docs");
		swaggerResource.setSwaggerVersion(version);
		return swaggerResource;
	}

}
