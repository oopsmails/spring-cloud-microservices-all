package com.oopsmails.spring.cloud.microservices.departmentservice.filter;

import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LogContextInitializingFilter extends OncePerRequestFilter {
    private static final String CORRELATION_ID = "CorrelationId";
    private static final String TRACEABILITY_ID = "TraceabilityID";

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {

        createMDCContext(httpServletRequest);
        try {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } finally {
            MDC.clear();
        }
    }

    private void createMDCContext(HttpServletRequest httpServletRequest) {
        //correlation id
        String correlationId = httpServletRequest.getHeader(CORRELATION_ID);
        MDC.put(CORRELATION_ID, correlationId != null ? correlationId : "");

        //traceability ID
        String traceabilityID = httpServletRequest.getHeader(TRACEABILITY_ID);
        MDC.put(TRACEABILITY_ID, traceabilityID != null ? traceabilityID : "");

        Object userId = httpServletRequest.getAttribute("userId"); // to be set by oAuth filter
        MDC.put("UserId", userId != null ? userId.toString() : "");
    }
}
