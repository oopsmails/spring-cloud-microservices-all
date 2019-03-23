package com.oopsmails.spring.cloud.microservices.employeeservice.restinterceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OAuthPassing {
    Class<? extends HttpHeaderHandler> value();
}
