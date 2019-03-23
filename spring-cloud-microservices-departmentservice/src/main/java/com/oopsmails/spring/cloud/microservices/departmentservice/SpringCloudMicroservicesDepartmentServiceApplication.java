package com.oopsmails.spring.cloud.microservices.departmentservice;

import com.oopsmails.spring.cloud.microservices.departmentservice.model.Department;
import com.oopsmails.spring.cloud.microservices.departmentservice.repository.DepartmentRepository;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.auth.BasicAuthRequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableOAuth2Sso
@EnableSwagger2
@EnableResourceServer

//@SpringBootApplication
//@EnableDiscoveryClient
//@EnableOAuth2Client
//@EnableSwagger2
public class SpringCloudMicroservicesDepartmentServiceApplication {

    @Value("${security.oauth2.client.access-token-uri}")
    private String accessTokenUri;
    @Value("${security.oauth2.client.client-id}")
    private String clientId;
    @Value("${security.oauth2.client.client-secret}")
    private String clientSecret;
    @Value("${security.oauth2.client.scope}")
    private String scope;

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudMicroservicesDepartmentServiceApplication.class, args);
    }

    @Bean
    public Docket swaggerPersonApi10() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.oopsmails.spring.cloud.microservices.departmentservice.controller"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(new ApiInfoBuilder().version("1.0").title("Department API").description("Documentation Department API v1.0").build());
    }

    @Bean
    DepartmentRepository repository() {
        DepartmentRepository repository = new DepartmentRepository();
        repository.add(new Department(1L, "Development"));
        repository.add(new Department(1L, "Operations"));
        repository.add(new Department(2L, "Development"));
        repository.add(new Department(2L, "Operations"));
        return repository;
    }

    //--------------------------------------------------------------------------------

//    @Bean
//    RequestInterceptor oauth2FeignRequestInterceptor() {
//        return new OAuth2FeignRequestInterceptor(new DefaultOAuth2ClientContext(), resource());
//    }

//    private OAuth2ProtectedResourceDetails resource() {
//        ResourceOwnerPasswordResourceDetails resourceDetails = new ResourceOwnerPasswordResourceDetails();
//        resourceDetails.setUsername("piomin");
//        resourceDetails.setPassword("piot123");
//        resourceDetails.setAccessTokenUri(accessTokenUri);
//        resourceDetails.setClientId(clientId);
//        resourceDetails.setClientSecret(clientSecret);
//        resourceDetails.setGrantType("password");
//        resourceDetails.setScope(Arrays.asList(scope));
//        return resourceDetails;
//    }

//    private OAuth2ProtectedResourceDetails resource() {
//        ClientCredentialsResourceDetails resourceDetails = new ClientCredentialsResourceDetails();
//        resourceDetails.setAccessTokenUri(accessTokenUri);
//        resourceDetails.setClientId(clientId);
//        resourceDetails.setClientSecret(clientSecret);
//        resourceDetails.setScope(Arrays.asList(scope));
//        return resourceDetails;
//    }


    @Bean
    public RequestInterceptor requestTokenBearerInterceptor() {

        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) authentication.getDetails();
                requestTemplate.header("Authorization", "Bearer " + details.getTokenValue());
            }
        };
    }

//    @Bean
//    public RequestInterceptor basicAuthRequestInterceptor() {
//        return new BasicAuthRequestInterceptor("user", "password");
//    }


}
