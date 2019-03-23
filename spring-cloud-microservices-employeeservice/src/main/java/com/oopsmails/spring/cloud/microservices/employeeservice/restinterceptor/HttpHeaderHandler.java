package com.oopsmails.spring.cloud.microservices.employeeservice.restinterceptor;

import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;

public interface HttpHeaderHandler {
    void setApplicationContext(ApplicationContext ctx);

}
