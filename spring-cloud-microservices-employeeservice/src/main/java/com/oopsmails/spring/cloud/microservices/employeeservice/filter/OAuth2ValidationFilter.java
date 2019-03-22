package com.oopsmails.spring.cloud.microservices.employeeservice.filter;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;

@Component
public class OAuth2ValidationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain)
            throws ServletException, IOException {
        String primaryTokenValue = httpServletRequest.getHeader("Authorization");

        if (primaryTokenValue == null || primaryTokenValue.length() == 0) {
            logger.debug("Primary jwt token is missing");
            SecurityContextHolder.clearContext();
            this.handleErrorResponse(httpServletResponse, 401, "ERR-NoToken");
            return;
        }

        try {
            String token = getJwtToken(primaryTokenValue);
            String userId = getUserIdFromToken(token);

            httpServletRequest.setAttribute("userId", userId);
            httpServletRequest.setAttribute("authorization", primaryTokenValue);

            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userId, null));
        } catch (Exception e) {
            logger.error(e);
            this.handleErrorResponse(httpServletResponse, 401, "ERR-TokenNotValid?");
            return;
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    protected void handleErrorResponse(HttpServletResponse httpServletResponse, int responseCode, String errorCode) throws IOException {
        httpServletResponse.setContentType("application/json");
        logger.error(responseCode);
    }

    protected String getJwtToken(String authHeader) {
        String jwtToken = null;
        if (authHeader != null) {
            String[] parts = authHeader.split(" ");
            if (parts.length == 2) {
                String scheme = parts[0];
                String credentials = parts[1];
                Pattern pattern = Pattern.compile("^Bearer$", 2);
                if (pattern.matcher(scheme).matches()) {
                    jwtToken = credentials;
                }
            }
        }
        return jwtToken;
    }

    protected String getUserIdFromToken(String token) {
        return "hackUserId";
    }
}
