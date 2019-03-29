package com.oopsmails.spring.cloud.microservices.zuulproxy;

import com.oopsmails.spring.cloud.microservices.zuulproxy.filter.HttpLoggingFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableZuulProxy
@EnableSwagger2
public class SpringCloudMicroservicesZuulProxyApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudMicroservicesZuulProxyApplication.class, args);
	}

	@Bean
	public FilterRegistrationBean<HttpLoggingFilter> httpLoggingFilter() {
		HttpLoggingFilter httpLoggingFilter = new HttpLoggingFilter();
		FilterRegistrationBean<HttpLoggingFilter> registrationBean = new FilterRegistrationBean<>();

		registrationBean.setFilter(httpLoggingFilter);
		registrationBean.addUrlPatterns("/*", "/uaa/*");
		registrationBean.setName("httpLoggingFilter");
		return registrationBean;
	}

}
