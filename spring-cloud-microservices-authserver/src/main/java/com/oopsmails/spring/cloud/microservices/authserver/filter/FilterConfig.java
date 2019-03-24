package com.oopsmails.spring.cloud.microservices.authserver.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<HttpLoggingFilter> httpLoggingFilter() {
        HttpLoggingFilter httpLoggingFilter = new HttpLoggingFilter();
        FilterRegistrationBean<HttpLoggingFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(httpLoggingFilter);
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName("httpLoggingFilter");
        return registrationBean;
    }
}
