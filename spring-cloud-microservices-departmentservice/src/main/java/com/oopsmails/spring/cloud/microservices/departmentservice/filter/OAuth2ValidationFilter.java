package com.oopsmails.spring.cloud.microservices.departmentservice.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

@Order(FilterOrder.Constants.FILTER_OAUTH2_VALIDATION_VALUE)
@Slf4j
public class OAuth2ValidationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain)
            throws ServletException, IOException {
        logger.info(String.format("########################## OAuth2ValidationFilter: httpServletRequest = %s", httpServletRequest));

        String primaryTokenValue = httpServletRequest.getHeader("Authorization");

        if (primaryTokenValue == null || primaryTokenValue.length() == 0) {
            logger.error("Primary jwt token is missing");
            SecurityContextHolder.clearContext();
            this.handleErrorResponse(httpServletResponse, 401, "ERR-NoToken");
            return;
        }

        try {
            // Authentication object should already be in SecurityContextHolder
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            OAuth2AuthenticationDetails oAuth2AuthenticationDetails = (OAuth2AuthenticationDetails) authentication.getDetails();
            HashMap<String, Object> decodedDetails = (LinkedHashMap) oAuth2AuthenticationDetails.getDecodedDetails();

            httpServletRequest.setAttribute("authorization", primaryTokenValue);
            httpServletRequest.setAttribute("token", oAuth2AuthenticationDetails.getTokenValue());
            httpServletRequest.setAttribute("tokenType", oAuth2AuthenticationDetails.getTokenType());
            httpServletRequest.setAttribute("sessionId", oAuth2AuthenticationDetails.getSessionId());
            httpServletRequest.setAttribute("remoteAddress", oAuth2AuthenticationDetails.getRemoteAddress());
            httpServletRequest.setAttribute("userId", decodedDetails.get("client_id"));
            httpServletRequest.setAttribute("authorization", primaryTokenValue);
        } catch (Exception e) {
            logger.error(e);
            this.handleErrorResponse(httpServletResponse, 401, "ERR-TokenNotValid?");
            return;
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
        logger.info(String.format("########################## OAuth2ValidationFilter: httpServletResponse = %s", httpServletResponse));
    }

    protected void handleErrorResponse(HttpServletResponse httpServletResponse, int responseCode, String errorCode) throws IOException {
        httpServletResponse.setContentType("application/json");
        logger.error(responseCode);
    }

}
