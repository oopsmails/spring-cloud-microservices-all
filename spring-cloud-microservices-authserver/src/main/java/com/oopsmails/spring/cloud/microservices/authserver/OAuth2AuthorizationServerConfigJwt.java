package com.oopsmails.spring.cloud.microservices.authserver;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServerConfigJwt extends AuthorizationServerConfigurerAdapter {

    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;

    @Override
    public void configure(final AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("permitAll()")
        ;
    }

    @Override
    public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
        // @formatter:off
        clients.inMemory()
                .withClient("sampleClientId")
                .authorizedGrantTypes("implicit", "client_credentials")
                .scopes("read", "write", "foo", "bar")
                .autoApprove(false)
                .accessTokenValiditySeconds(3600)

                .and()
                .withClient("fooClientIdPassword")
                .secret("$2a$10$2cH8fiWcnJhNyXbFnpEsE.o8opOLMhWLd7J61JUnuroEUOSBh/JLK") // my_secret
                .authorizedGrantTypes("password", "authorization_code", "refresh_token", "client_credentials")
                .scopes("foo", "read", "write")
                .accessTokenValiditySeconds(3600)
                // 1 hour
                .refreshTokenValiditySeconds(2592000)
                // 30 days

                .and()
                .withClient("barClientIdPassword")
                .secret("$2a$10$2cH8fiWcnJhNyXbFnpEsE.o8opOLMhWLd7J61JUnuroEUOSBh/JLK") // my_secret
                .authorizedGrantTypes("password", "authorization_code", "refresh_token", "client_credentials")
                .scopes("bar", "read", "write")
                .accessTokenValiditySeconds(3600)
                // 1 hour
                .refreshTokenValiditySeconds(2592000) // 30 days

                .and()
                .withClient("demo")
                .authorizedGrantTypes("implicit", "client_credentials")
                .scopes("read", "write", "foo", "bar")
                .autoApprove(true)
                .accessTokenValiditySeconds(3600)

                .and()
                .withClient("authorizationCodeClient")
                .secret("$2a$10$2cH8fiWcnJhNyXbFnpEsE.o8opOLMhWLd7J61JUnuroEUOSBh/JLK") // my_secret
                .authorizedGrantTypes("authorization_code", "client_credentials")
                .scopes("messagesCtrl", "read", "write")
                .autoApprove(true) // autoApprove is set to true so that we’re not redirected and promoted to manually approve any scopes.

                .and()
                .withClient("clientCredentialsClient")
                .secret("$2a$10$2cH8fiWcnJhNyXbFnpEsE.o8opOLMhWLd7J61JUnuroEUOSBh/JLK") // my_secret
                .authorizedGrantTypes("client_credentials")
                .scopes("messagesCtrl", "read", "write")
                .autoApprove(true)

                .and()
                .withClient("demops")
                .secret("$2a$10$2cH8fiWcnJhNyXbFnpEsE.o8opOLMhWLd7J61JUnuroEUOSBh/JLK") // my_secret
                .authorizedGrantTypes("implicit", "password", "authorization_code", "refresh_token", "client_credentials")
                .scopes("messagesCtrl", "read", "write")
                .autoApprove(true)
                .accessTokenValiditySeconds(7200)
                // 1 hour
                .refreshTokenValiditySeconds(2592000)
        ;
        // @formatter:on
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        return defaultTokenServices;
    }

    @Override
    public void configure(final AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
//		endpoints.authenticationManager(authenticationManager);

        final TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), accessTokenConverter()));
        endpoints.tokenStore(tokenStore())
                .tokenEnhancer(tokenEnhancerChain)
                .authenticationManager(authenticationManager);
    }

    // @formatter:off
    @Bean
    public TokenStore tokenStore() {
//        return new JdbcTokenStore(dataSource);
//        return new InMemoryTokenStore();
        return new JwtTokenStore(accessTokenConverter());
    }
    // @formatter:on

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        final JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        // converter.setSigningKey("123");
        final KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("mytest.jks"), "mypass".toCharArray());
        converter.setKeyPair(keyStoreKeyFactory.getKeyPair("mytest"));
        return converter;
    }

    @Bean
    public TokenEnhancer tokenEnhancer() {
        return new CustomTokenEnhancer();
    }

}
