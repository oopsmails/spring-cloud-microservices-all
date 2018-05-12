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


--> change to JWT instead of default
OAuth2AuthorizationServerConfigJwt


curl -X POST --user 'demops:password' -d 'grant_type=password&username=user&password=password' http://localhost:9999/uaa/oauth/token

::> Note, the client secret is not included here under the assumption that most of the use cases for password grants will be mobile or desktop apps, where the secret cannot be protected.

curl -X POST --user 'demops' -d 'grant_type=password&username=user&password=password' http://localhost:9999/uaa/oauth/token



--> add "spring-security-oauth2-autoconfigure" in pom.xml, will initialize authentication!
Not even do @EnableResourceServer!


-->
curl -X GET --user 'demops:password' -d 'grant_type=password&username=user&password=password' http://localhost:9999/uaa/oauth/userinfo


employee-service.yml

key-uri: ${ssoServiceUrl}/oauth/token_key

http://localhost:9999/uaa/oauth/token_key


http://localhost:9999/employee
http://localhost:18084

http://localhost:8888/employee-service/default





--> @Bean
public static NoOpPasswordEncoder passwordEncoder() {
    return NoOpPasswordEncoder.getInstance();
}

=================================================================================

==> in Auth Server, to make /oauth/check_token working!

oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("permitAll()");


http://localhost:9999/uaa/oauth/check_token?token=xxxxxxxxxxxxx

--> in Zuul
employee:
      path: /employee/**
      sensitiveHeaders: Cookie,Set-Cookie <-------- adding this, otherwise, 18084 (from inside) is fine, but from 9999 is not fine!
      serviceId: employee-service
      stripPrefix: false <-------------- this should be revisited 

--> 

============================================================


D:\Downloaded\Tech\spring-boot2-oauth2-master\src\main\java\com\smedzl\example\config\WebSecurityConfig.java

public class WebSecurityConfig extends WebSecurityConfigurerAdapter
@Override
protected void configure(HttpSecurity http) throws Exception {

	// @formatter:off
	http
			// enable cors
			.cors().and().requestMatchers().antMatchers("/oauth/**", "/*").and()
			// These from the above are secured by the following way
			.authorizeRequests().antMatchers("/", LOGIN).permitAll()
			// These from the rest are secured by the following way
			.anyRequest().authenticated().and()
			// Set login page
			.formLogin().loginPage(LOGIN).permitAll().defaultSuccessUrl(PROFILE)
			// Set logout handling
			.and().logout().logoutSuccessUrl(LOGOUT_SUCCESS);
	// @formatter:on
}
	

@EnableResourceServer
http.requestMatchers().antMatchers("/user").and().authorizeRequests().anyRequest().authenticated();




---------------

https://github.com/spring-projects/spring-security-oauth/issues/183

@Override
public Optional<Authentication> getAuthentication() {
    return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
}

@Override
public Optional<String> getCurrentUserId() {
    Optional<Authentication> authentication = getAuthentication();
    return authentication.map(a -> {
        OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) a.getDetails();
        return (String) ((Map) details.getDecodedDetails()).get("user_id");
    });
}



================================================

=====> 20180509:

==> Fix Swagger for Employee Service,

--> EmployeeController:

@RestController
//@RequestMapping("/employee-api") <---- diff here not needed
public class EmployeeController

--> OAuth2ResourceServerConfigJwt: differ swagger path "/v2/**" (which is no auth needed) and real api "/employee-api/**"

@Override
public void configure(final HttpSecurity http) throws Exception {
	// @formatter:off
	http.sessionManagement()
		.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) //SessionCreationPolicy.STATELESS
		.and()
		.authorizeRequests()

		.antMatchers(HttpMethod.GET, "/v2/**") <------------------------ make swagger permitAll
		.permitAll()

		.antMatchers(HttpMethod.GET, "/**")
		.access("#oauth2.hasScope('read')")

		.antMatchers(HttpMethod.POST, "/employee-api/**")
		.access("#oauth2.hasScope('write')")

		.antMatchers("/abc")
		.hasRole("ADMIN")
	;
	// @formatter:on
}

--> 


zuul:
  routes:
    auth:
      path: /uaa/**
      sensitiveHeaders: Cookie,Set-Cookie
      serviceId: auth-server
      stripPrefix: true
    department:
      path: /department/**
      serviceId: department-service
    employee:
      path: /employee/**
      sensitiveHeaders: Cookie,Set-Cookie
      serviceId: employee-service
      stripPrefix: true <---------------------- make /employee/** is only for zuul
    organization:
      path: /organization/**
      serviceId: organization-service

----------------------

--> ProxyApi, filter out auth-server from swagger!



-------------------------------
http://localhost:9999/uaa/login


http://localhost:9999/uaa/oauth/authorize


http://localhost:9999/uaa/oauth/authorize?response_type=token&client_id=demops&state=B4u93Ap1tWyMe9xS1rbarNI8f3SSZ9vdjAEFUfSo&redirect_uri=http://localhost:4203/&scope=read write


===============================================

20180510:


==> backend: intellij
C:\Github\spring-cloud-microservices-all\

Start:

All Apps,



C:\Github\springboot-configs\spring-cloud-microservices\

Start:


SpringCloudMicroservicesResourceApplication
SpringCloudMicroservicesClientApplication



==> frontend: vs code

angular-4203-cloud-implicit     
angular-4204-cloud-password     
angular-4205-cloudAll-implicit     
angular-4206-cloudAll-password  


C:\Temp\tttt\spring-cloud-microservices-all-oauth-ui-implicit
C:\Temp\tttt\spring-cloud-microservices-all-oauth-ui-password

C:\Temp\tttt\spring-cloud-oauth-ui-implicit
C:\Temp\tttt\spring-cloud-oauth-ui-password


==> Next: try

C:\Github\springboot-configs\spring-cloud-microservices\
SpringCloudMicroservicesClientApplication

@EnableOAuth2Sso
@RibbonClient(name = "resource")



================================================

<br><a href="http://localhost:8888/">ConfigServer:8888/</a>&nbsp;&nbsp;&nbsp;&nbsp;
<br><a href="http://localhost:8761/">Eureka:8761/</a>&nbsp;&nbsp;&nbsp;&nbsp;
<br><a href="http://localhost:9999/">Zuul:9999/</a>&nbsp;&nbsp;&nbsp;&nbsp;
<br><a href="http://localhost:18080/">localhost:18080(Organization)</a>&nbsp;&nbsp;&nbsp;&nbsp;
<br><a href="http://localhost:18082/">localhost:18082(Department)</a>&nbsp;&nbsp;&nbsp;&nbsp;
<br><a href="http://localhost:18084/">localhost:18084(Employee)</a>&nbsp;&nbsp;&nbsp;&nbsp;

<br>
<br>

<br><a href="http://localhost:4200/">angular-4200</a>&nbsp;&nbsp;&nbsp;&nbsp;
<a href="http://localhost:4201/">angular-4201</a>&nbsp;&nbsp;&nbsp;&nbsp;
<a href="http://localhost:4202/">angular-4202</a>&nbsp;&nbsp;&nbsp;&nbsp;
<a href="http://localhost:4203/">angular-4203-cloud-implicit</a>&nbsp;&nbsp;&nbsp;&nbsp;
<a href="http://localhost:4204/">angular-4204-cloud-password</a>&nbsp;&nbsp;&nbsp;&nbsp;
<a href="http://localhost:4205/">angular-4205-cloudAll-implicit</a>&nbsp;&nbsp;&nbsp;&nbsp;
<a href="http://localhost:4206/">angular-4206-cloudAll-password</a>&nbsp;&nbsp;&nbsp;&nbsp;


================================================

==> Oauth2


Note: Previously, it was recommended that browser-based apps use the "Implicit" flow, which returns an access token 
immediately and does not have a token exchange step. In the time since the spec was originally written, the industry 
best practice has changed to recommend that the authorization code flow be used without the client secret. This 
provides more opportunities to create a secure flow, such as using the state parameter. 

----> Authorization Code for apps running on a web server, browser-based and mobile apps

1. Open your browser and to visit the authorization endpoint
http://localhost:9999/uaa/oauth/authorize?response_type=code&client_id=authorizationCodeClient&redirect_uri=http://localhost:4205/&scope=messagesCtrl&state=1234zyx
ok, http://localhost:9999/uaa/oauth/authorize?response_type=code&client_id=authorizationCodeClient&redirect_uri=http://localhost:4205/

OAuth2AuthorizationServerConfigJwt, if .autoApprove(true), then not redirected and promoted to manually approve any scopes.

2. After the login process (login: user password: password), you will be redirected to http://example.com/?code=CODE <-- this is the code that you should use in the next request
http://localhost:4205/?code=yusIXE

3. now you get the token
curl -X POST --user 'authorizationCodeClient:my_secret' -d 'grant_type=authorization_code&code=rueRhK&redirect_uri=http://localhost:4205/' http://localhost:9999/uaa/oauth/token


----> Password for logging in with a username and password

curl -X POST --user 'demops:password' -d 'grant_type=password&username=user&password=password' http://localhost:9999/uaa/oauth/token

curl -X POST --user 'demops' -d 'grant_type=password&username=user&password=password' http://localhost:9999/uaa/oauth/token

----> Client credentials for application access
In some cases, applications may need an access token to act on behalf of themselves rather than a user. For example, 
the service may provide a way for the application to update their own information such as their website URL or icon, 
or they may wish to get statistics about the users of the app. In this case, applications need a way to get an access 
token for their own account, outside the context of any specific user. OAuth provides the client_credentials grant 
type for this purpose.

curl -X POST --user 'clientCredentialsClient:my_secret' -d 'grant_type=client_credentials' http://localhost:9999/uaa/oauth/token


----> Implicit was previously recommended for clients without a secret, but has been superseded by using the 
Authorization Code grant with no secret.





================================================

================================================


================================================

================================================


================================================

================================================


================================================



==> thanks, part 1
https://piotrminkowski.wordpress.com/2018/04/26/quick-guide-to-microservices-with-spring-boot-2-0-eureka-and-spring-cloud/comment-page-1/#comment-574

https://github.com/piomin/sample-spring-microservices-new



