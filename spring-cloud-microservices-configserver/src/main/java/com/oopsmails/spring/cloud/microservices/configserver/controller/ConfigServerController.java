package com.oopsmails.spring.cloud.microservices.configserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigServerController {
    @Value("${test.property:test.prop.defaultvalue}")
    private String testProperty;

    @Value("${test.local.property:test.local.prop.defaultvalue}")
    private String localTestProperty;

    @Autowired
    private PropertyConfiguration propertyConfiguration;

    @RequestMapping("/")
    public String test() {
        StringBuilder builder = new StringBuilder();
        builder.append("global property - ").append(testProperty).append("\n")
                .append("local property - ").append(localTestProperty).append("\n")
                .append("property configuration value - ").append(propertyConfiguration.getProperty());
        return builder.toString();
    }
}
