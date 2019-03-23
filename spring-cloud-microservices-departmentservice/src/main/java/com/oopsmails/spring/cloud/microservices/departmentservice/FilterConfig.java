package com.oopsmails.spring.cloud.microservices.departmentservice;

import com.oopsmails.spring.cloud.microservices.departmentservice.filter.HttpLoggingFilter;
import com.oopsmails.spring.cloud.microservices.departmentservice.filter.LogContextInitializingFilter;
import com.oopsmails.spring.cloud.microservices.departmentservice.filter.OAuth2ValidationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<OAuth2ValidationFilter> oAuth2ValidationFilter() {
        FilterRegistrationBean<OAuth2ValidationFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new OAuth2ValidationFilter());
        registrationBean.addUrlPatterns("/*", "/employee/*");
        registrationBean.setName("oAuthValidationFilter");
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<LogContextInitializingFilter> logContextInitializingFilter() {
        FilterRegistrationBean<LogContextInitializingFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new LogContextInitializingFilter());
        registrationBean.addUrlPatterns("/*", "/employee/*");
        registrationBean.setName("logContextInitializingFilter");
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<HttpLoggingFilter> httpLoggingFilter() {
        HttpLoggingFilter httpLoggingFilter = new HttpLoggingFilter();
        FilterRegistrationBean<HttpLoggingFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(httpLoggingFilter);
        registrationBean.addUrlPatterns("/*", "/employee/*");
        registrationBean.setName("httpLoggingFilter");
        return registrationBean;
    }
}
