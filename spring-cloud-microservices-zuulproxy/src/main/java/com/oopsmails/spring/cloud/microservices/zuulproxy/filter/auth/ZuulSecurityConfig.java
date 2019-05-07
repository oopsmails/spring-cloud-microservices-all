package com.oopsmails.spring.cloud.microservices.zuulproxy.filter.auth;

import com.oopsmails.spring.cloud.microservices.zuulproxy.filter.auth.PreAuthIdAuthenticationConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableWebSecurity
public class ZuulSecurityConfig extends WebSecurityConfigurerAdapter {
    private static final Set<String> exemptionList = new HashSet<>();
    static {
        exemptionList.add("/uaa/**");
        exemptionList.add("/employee/**");
    }

    @Autowired
    private ZuulProperties zuulProperties;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String[] patterns = zuulProperties.getRoutes().values()
                .stream().map(ZuulProperties.ZuulRoute::getPath).toArray(String[]::new);

        patterns = Arrays.stream(patterns)
                .filter(configured -> !exemptionList.contains(configured))
                .toArray(String[]::new);

        http.apply(new PreAuthIdAuthenticationConfigurer<>())
                .and().headers().frameOptions().sameOrigin()
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and().authorizeRequests()
                .antMatchers(patterns).authenticated()
                .anyRequest().permitAll()
                .and().formLogin()
                .and().logout()
                .and().csrf().disable();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // TODO: need dig more
        return username -> User.withUsername(username).password("password").authorities("Authenticated").build();
    }

}
