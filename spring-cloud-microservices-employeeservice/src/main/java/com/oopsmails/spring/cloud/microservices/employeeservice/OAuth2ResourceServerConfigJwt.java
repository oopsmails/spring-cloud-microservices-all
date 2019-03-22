package com.oopsmails.spring.cloud.microservices.employeeservice;

import com.oopsmails.spring.cloud.microservices.employeeservice.filter.OAuth2ValidationFilter;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.io.IOException;

@Configuration
@EnableResourceServer
@EnableWebSecurity
//@Order(SecurityProperties.BASIC_AUTH_ORDER)
public class OAuth2ResourceServerConfigJwt extends ResourceServerConfigurerAdapter {

    @Autowired
    private CustomAccessTokenConverter customAccessTokenConverter;

    @Autowired
    private OAuth2ValidationFilter oAuth2ValidationFilter;

    @Override
    public void configure(final HttpSecurity http) throws Exception {
        // @formatter:off
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) //SessionCreationPolicy.STATELESS

                .and()
                .authorizeRequests()

                .antMatchers(HttpMethod.GET, "/employee/v2/**")
                .permitAll()

                .antMatchers(HttpMethod.GET, "/employee/**")
                .access("#oauth2.hasScope('read')")

                .antMatchers(HttpMethod.POST, "/**")
                .access("#oauth2.hasScope('write')")

                .antMatchers(HttpMethod.GET, "/**")
                .access("#oauth2.hasScope('read')")

                .antMatchers("/abc").hasRole("ADMIN"); // testing

//			.anyRequest().permitAll();
        // @formatter:on
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources)
            throws Exception {
        resources
                .tokenStore(tokenStore())
                .tokenServices(tokenServices())
                .authenticationManager(authenticationManager());
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        final OAuth2AuthenticationManager oAuth2AuthenticationManager = new OAuth2AuthenticationManager();
        oAuth2AuthenticationManager.setTokenServices(tokenServices());
        return oAuth2AuthenticationManager;
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        return defaultTokenServices;
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtTokenEnhancer());
    }

    @Bean
    @Primary
    public JwtAccessTokenConverter jwtTokenEnhancer() {
        final JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setAccessTokenConverter(customAccessTokenConverter);

        // converter.setSigningKey("123");
        final Resource resource = new ClassPathResource("public.txt");
        String publicKey = null;
        try {
            publicKey = IOUtils.toString(resource.getInputStream());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        converter.setVerifierKey(publicKey);
        return converter;
    }

    @Bean
    public FilterRegistrationBean oAuthFilterRegistration() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();

        registrationBean.setFilter(oAuth2ValidationFilter);
        registrationBean.addUrlPatterns("/**", "/employee/**");
        registrationBean.setName("oAuthValidationFilter");
        return registrationBean;
    }
}
