# spring-cloud-microservices

================================================

com.oopsmails.spring.cloud.microservices
spring-cloud-microservices

SpringCloudMicroservices


com.oopsmails.spring.cloud.microservices.configserver
spring-cloud-microservices-configserver

C:\\Github\\spring-cloud-microservices-all\\spring-cloud-microservices-configserver\\src\\main\\resources\\config


http://localhost:8888/discovery-service/default --> working


==> need spring.profiles.active=native to make it working if uri: classpath:/config


================================================


department-service.yml
8091 --> 18082

discovery-service
8061 --> 8761

employee-service
8090 --> 18084, 18085
spring.profiles.active=instance2

gateway-service
8060 --> 9998

organization-service
8092 --> 18080

proxy-service
8060 --> 9999


http://localhost:8761/

http://localhost:9999/swagger-ui.html

http://localhost:9999/organization
http://localhost:9999/department
http://localhost:9999/employee

http://localhost:9999/organization/1/with-departments-and-employees




================================================


--------------

curl -X POST --user 'demops:password' -d 'grant_type=password&username=user&password=password' http://localhost:7777/oauth/token

curl -X POST --user 'demops:password' -d 'grant_type=password&username=user&password=password' http://localhost:9999/uaa/oauth/token


============================
auth-server: setup and working fine, 20180504


==> Problem: java.lang.IllegalArgumentException: There is no PasswordEncoder mapped for the id "null"

Solution: resolved by adding 
@Bean
public BCryptPasswordEncoder passwordEncoder()

==> Problem:
2018-05-04 13:45:46.835  WARN 16892 --- [nio-7777-exec-1] o.s.s.c.bcrypt.BCryptPasswordEncoder     : Encoded password does not look like BCrypt

Solution: resolved by encoding password in WebSecurityConfig:globalUserDetails(final AuthenticationManagerBuilder auth)

Note: both "secret" and user "password" should be BCrypt-ed
$2a$10$1XqtAJZ9EXiuCCK2gy6gTuUEyYFsB97g5op1AXxRHQibf2mNe4x0i

==> NO /uaa below for 7777, because calling from inside
curl -X POST --user 'demops:password' -d 'grant_type=password&username=user&password=password' http://localhost:7777/oauth/token


==> returning 401: Unauthorized,
curl -X POST --user 'demops:password' -d 'grant_type=password&username=user&password=password' http://localhost:9999/uaa/oauth/token

security.oauth2.resource.token-info-uri: http://localhost:9999/uaa/oauth/check_token
is from outside.

Solution: in log, seeing mapping "/uaa/oauth/check_token" to "/oauth/check_token"

gateway-service.yml
- RewritePath=/uaa/(?<path>.*), /$\{path}

proxy-service.yml
- stripPrefix: true <----------- this will omit /oath from Zuul


==> 401 continue ...

debug: BasicAuthenticationFilter, 
protected void doFilterInternal()
::> String header = request.getHeader("Authorization");

Authorization header is filtered out by Zuul server,

Solution: proxy-service.yml

sensitiveHeaders: Cookie,Set-Cookie

routes:
    auth:
      path: /uaa/**
      sensitiveHeaders: Cookie,Set-Cookie <-------- adding this 
      serviceId: auth-server
      stripPrefix: true

--

sensitiveHeaders: Cookie,Set-Cookie,Authorization
This is default and is a black list, so, overwrite it to allow "Authorization" passed to auth-server!


--> Very helpful thread, thanks!

https://github.com/spring-projects/spring-boot/issues/12346

https://github.com/Smedzlatko/spring-boot2-oauth2


================================================


================================================


================================================



==> thanks, part 1
https://piotrminkowski.wordpress.com/2018/04/26/quick-guide-to-microservices-with-spring-boot-2-0-eureka-and-spring-cloud/comment-page-1/#comment-574

https://github.com/piomin/sample-spring-microservices-new



