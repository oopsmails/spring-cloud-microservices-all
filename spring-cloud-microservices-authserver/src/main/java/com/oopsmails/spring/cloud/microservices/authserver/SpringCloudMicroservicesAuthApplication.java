package com.oopsmails.spring.cloud.microservices.authserver;

import org.apache.catalina.filters.RequestDumperFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
//@EnableAuthorizationServer // Moved to OAuth2AuthorizationServerConfigJwt
@EnableEurekaClient
@RestController
@SessionAttributes("authorizationRequest")
public class SpringCloudMicroservicesAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudMicroservicesAuthApplication.class, args);
    }

    @Profile("!cloud")
    @Bean
    RequestDumperFilter requestDumperFilter() {
        return new RequestDumperFilter();
    }

    @Configuration
    static class MvcConfig extends WebMvcConfigurerAdapter {

        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
            registry.addViewController("login").setViewName("login");
//            registry.addViewController("/oauth/confirm_access").setViewName("authorize"); // no authorize.html
            registry.addViewController("/").setViewName("index");
        }
    }

}
