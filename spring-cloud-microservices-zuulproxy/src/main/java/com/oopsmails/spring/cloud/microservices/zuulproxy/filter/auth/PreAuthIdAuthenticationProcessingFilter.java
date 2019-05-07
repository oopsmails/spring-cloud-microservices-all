package com.oopsmails.spring.cloud.microservices.zuulproxy.filter.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
public class PreAuthIdAuthenticationProcessingFilter extends AbstractPreAuthenticatedProcessingFilter {

    private static final String PRE_AUTH_ID = "com.oopsmails.sso.Session.PREAUTHID";
    private static final String BROWSER = "com.oopsmails.sso.Session.BROWSER";
    private static final String IP = "com.oopsmails.sso.Session.IP";

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        String preAuthId = getPreAuthId(request);
        log.info("PreAuthenticatedPrincipal {}", preAuthId);
        return preAuthId;
    }

    private String getPreAuthId(HttpServletRequest request) {
        return request.getParameter("oopsmails.preauthid");
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute(PRE_AUTH_ID, getPreAuthId(request));
        session.setAttribute(IP, request.getRemoteAddr());
        session.setAttribute(BROWSER, request.getHeader("User-Agent"));
        return session.getId();
    }

}
