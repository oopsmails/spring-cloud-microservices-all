package com.oopsmails.spring.cloud.microservices.zuulproxy.filter.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.X509Configurer;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

public class PreAuthIdAuthenticationConfigurer<H extends HttpSecurityBuilder<H>> extends AbstractHttpConfigurer<X509Configurer<H>, H> {

    private PreAuthIdAuthenticationProcessingFilter preAuthIdAuthenticationProcessingFilter;

    private AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> authenticationUserDetailsService;

    public PreAuthIdAuthenticationConfigurer<H> preAuthIdAuthenticationProcessingFilter(
            PreAuthIdAuthenticationProcessingFilter preAuthIdAuthenticationProcessingFilter) {
        this.preAuthIdAuthenticationProcessingFilter = preAuthIdAuthenticationProcessingFilter;
        return this;
    }

    @Override
    public void init(H http) throws Exception {
        PreAuthenticatedAuthenticationProvider authenticationProvider = new PreAuthenticatedAuthenticationProvider();
        authenticationProvider.setPreAuthenticatedUserDetailsService(getAuthenticationUserDetailsService(http));

        http
                .authenticationProvider(authenticationProvider)
                .setSharedObject(AuthenticationEntryPoint.class, new Http403ForbiddenEntryPoint());
    }

    @Override
    public void configure(H http) throws Exception {
        PreAuthIdAuthenticationProcessingFilter filter = getFilter(http
                .getSharedObject(AuthenticationManager.class));
        http.addFilter(filter);
    }

    private PreAuthIdAuthenticationProcessingFilter getFilter(AuthenticationManager authenticationManager) {
        if (preAuthIdAuthenticationProcessingFilter == null) {
            preAuthIdAuthenticationProcessingFilter = new PreAuthIdAuthenticationProcessingFilter();
            preAuthIdAuthenticationProcessingFilter.setAuthenticationManager(authenticationManager);
            preAuthIdAuthenticationProcessingFilter = postProcess(preAuthIdAuthenticationProcessingFilter);
        }

        return preAuthIdAuthenticationProcessingFilter;
    }

    private AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> getAuthenticationUserDetailsService(H http) {
        if (authenticationUserDetailsService == null) {
            userDetailsService(http.getSharedObject(UserDetailsService.class));
        }
        return authenticationUserDetailsService;
    }


    public PreAuthIdAuthenticationConfigurer<H> userDetailsService(UserDetailsService userDetailsService) {
        UserDetailsByNameServiceWrapper<PreAuthenticatedAuthenticationToken> authenticationUserDetailsService = new UserDetailsByNameServiceWrapper<>();
        authenticationUserDetailsService.setUserDetailsService(userDetailsService);
        return authenticationUserDetailsService(authenticationUserDetailsService);
    }

    public PreAuthIdAuthenticationConfigurer<H> authenticationUserDetailsService(
            AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> authenticationUserDetailsService) {
        this.authenticationUserDetailsService = authenticationUserDetailsService;
        return this;
    }


}
