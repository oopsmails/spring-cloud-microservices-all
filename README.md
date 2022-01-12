# spring-cloud-microservices

## 20220112: Revisit this app

- upgraded to SpringBoot 2.5.5
- SpringCloud 2020.0.4
- demolished zuul-server
- using spring-gateway in future

- Run app in order
    - config-server
    - eureka-server
    - auth-server, gateway-server, employee-service
    - organization-service, department-service


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
--> add this JVM option: -Dspring.profiles.active=instance2

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

====> Eureka not deregister instance! 

employee-service
spring.profiles.active=instance2
--> after started instance2, then round robin when calling http://localhost:9999/employee
--> but, if stopped instance2, then always failing once for every two calls because one of two instances is DOWN.
--> Eureka still show the status of the stopped one as UP.
--> Need more configuration!

https://stackoverflow.com/questions/32616329/eureka-never-unregisters-a-service:

```
I made service de-registration work by setting the below values

Eureka server application.yml:

eureka:
  server:
    enableSelfPreservation: false

--> later eureka page, seeing "THE SELF PRESERVATION MODE IS TURNED OFF. THIS MAY NOT PROTECT INSTANCE EXPIRY IN CASE OF NETWORK/OTHER PROBLEMS."


Service application.yml:

eureka:
  instance:
    leaseRenewalIntervalInSeconds: 1
    leaseExpirationDurationInSeconds: 2
The full example is here https://github.com/ExampleDriven/spring-cloud-eureka-example

And more, add following dependency in the pom.xml of Eureka server
https://github.com/spring-cloud/spring-cloud-netflix/issues/2696

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

```

--> even like this, always, the first call is good, the second call will fail and all calls after will be ok.



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
Use SpringCloudMicroservicesAuthApplicationTest to get BCrypt-ed password.

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

**20190322: **

Authorization header is filtered out by Zuul server,

Solution: bootstrap.yml of Zuul server,

sensitiveHeaders: Cookie,Set-Cookie

routes:
    auth:
      path: /uaa/**
      sensitiveHeaders: Cookie,Set-Cookie <-------- adding this 
      serviceId: auth-server
      stripPrefix: true


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

--> ok
curl -X POST --user 'demops:my_secret' -d 'grant_type=password&username=user&password=password' http://localhost:9999/uaa/oauth/token

--> not prompt for secret??
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


And the application.yml:

server:
    port: 8082
    servlet:
        context-path: /ui
    session:
      cookie:
        name: UISESSION
security:
  basic:
    enabled: false
  oauth2:
    client:
      clientId: SampleClientId
      clientSecret: secret
      accessTokenUri: http://localhost:8081/auth/oauth/token
      userAuthorizationUri: http://localhost:8081/auth/oauth/authorize
    resource:
      userInfoUri: http://localhost:8081/auth/user/me
spring:
  thymeleaf:
    cache: false
A few quick notes:


**we disabled the default Basic Authentication
accessTokenUri is the URI to obtain the Access Tokens
userAuthorizationUri is the authorization URI that users will be redirected to
userInfoUri the URI of user endpoint to obtain current user details**



================================================

20190320: Springboot config server, /encrypt endpoint

**Make sure correct version of JCl is used when generating jks**

C:\Program Files\Java\jdk1.8.0_121\jre\lib\security

local_policy.jar
US_export_policy.jar

**Use following to generate jks file**

For asymmetric encryption the value of encrypt.key should be a PEM-encoded string value or we can configure our custom keystore to use. Following is a sample to generate a keystore.

-- SET PATH=%PATH%;C:\Program Files\Java\jdk1.8.0_121\bin
-- JAVA_HOME "not working" in Windows 10, java -version, showing java9, not java version "1.8.0_121"
Need to go to "Environment Variables ...", set JAVA_HOME, and then in "Path", move the entry "%JAVA_HOME%\BIN" to the top!
Now, cmd, java -version, will show "1.8.0_121"

Otherwise, will get "unable to initialize due to invalid secret key", "java.security.invalidkeyexception: illegal key size"

keytool -genkeypair -alias oopsmails-config-server-key -keyalg RSA -dname "CN=Oopsmails Config Server,OU=OopsmailsSpring Cloud,O=Oopsmails" -keypass oopsmails-keypass -keystore oopsmails-config-server.jks -storepass oopsmails-storepass

**Copy jks file to source folder, configure in bootstrap.yml**

curl -X POST --data-urlencode testbody http://localhost:8888/encrypt

curl -X POST \
  http://localhost:8888/decrypt \
  --d 'AQB5O3qJFaQqzKb/pRbVDgdG03QNOsCFtV7OFc/hDSekbS42ubPlbVR7gAG5ycTQL7tA3oIAn+1Pdd/stp0YhIF9YjzcnVa21mLqRa0fO9mQ/oAM9l2lKNzIX6xW+wMtYC/faI0md8FBVqfO8XDfAFnKr99ZVXifVQqsQ62XIgFgBePQOk2KcuG8hQ7m4PZanUYixYv3CuKD/fleoqalF9k+IzDBBaQuO4tD4lAtM1OiRgTY1l25CI6/0gpRLRepmPpdUG0ri6CalKyCxSIpjFC9d/5a8eSChrOKSjWkIJZqh43X7xBXdKs5no1ocAxiQMrMmTf0GZ7r2UW/33nIWWRM1Pw1w+Q2lJiRlSSFAY5eQsT9+O1zrZ1ZCG+vceVmiuw='

curl -X POST \
  http://localhost:8888/decrypt \
  --data-urlencode 'AQB5O3qJFaQqzKb/pRbVDgdG03QNOsCFtV7OFc/hDSekbS42ubPlbVR7gAG5ycTQL7tA3oIAn+1Pdd/stp0YhIF9YjzcnVa21mLqRa0fO9mQ/oAM9l2lKNzIX6xW+wMtYC/faI0md8FBVqfO8XDfAFnKr99ZVXifVQqsQ62XIgFgBePQOk2KcuG8hQ7m4PZanUYixYv3CuKD/fleoqalF9k+IzDBBaQuO4tD4lAtM1OiRgTY1l25CI6/0gpRLRepmPpdUG0ri6CalKyCxSIpjFC9d/5a8eSChrOKSjWkIJZqh43X7xBXdKs5no1ocAxiQMrMmTf0GZ7r2UW/33nIWWRM1Pw1w+Q2lJiRlSSFAY5eQsT9+O1zrZ1ZCG+vceVmiuw='




================================================
20190321:

**Front end, need to run: npm run start, in order to pick up proxy.conf.json**
Should see this in terminal console:
Proxy created: /employee  ->  http://localhost:9999

**Update to Angular 7,**


install nvm4win, v1.1.7,

>nvm list available

>nvm install 10.15.3

>nvm use 10.15.3

>nvm list

  * 10.15.3 (Currently using 64-bit executable)
    7.9.0
    6.10.1
    6.9.1

>npm -v
6.4.1

>node -v
v10.15.3

------
npm uninstall -g angular-cli

npm install -g @angular/cli@latest

If your version is still old, then try the following command.
ng update @angular/cli


Now, check your Angular CLI version using the following command.

ng --version
-------

>npm uninstall -g angular-cli
up to date in 0.035s

>ng -v
'ng' is not recognized as an internal or external command,
operable program or batch file.

>npm install -g @angular/cli@latest
C:\nodejs\ng -> C:\nodejs\node_modules\@angular\cli\bin\ng
.........
+ @angular/cli@7.3.6
added 295 packages from 180 contributors in 29.999s


**in VS Code**

ng update @angular/cli --migrate-only --from=1.7.3

npm install

npm run start











================================================

**implicit flow**

Seeing login page from "http://localhost:9999/uaa/login" first.

**password flow**

Loading login page from "http://localhost:4206/login".



================================================


================================================

================================================

====> Maven problem: 
```
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.7.0:compile (default-compile) on project spring-cloud-microservices-departmentservice: Fatal error compiling: java.lang.ExceptionInInitializerError: com.sun.tools.javac.code.TypeTags -> [Help 1]
[ERROR]
```

Resolved by define JDK as 1.8 instead of 11 in Intellij.

====> ZuulSessionConfig: 

https://docs.spring.io/spring-session/docs/current-SNAPSHOT/reference/html5/

@EnableHazelcastHttpSession
@EnableSpringHttpSession

Spring Data Redis, which provides the abstractions of the Spring Data platform to Redis – the popular in-memory data structure store.



================================================



==> thanks, part 1
https://piotrminkowski.wordpress.com/2018/04/26/quick-guide-to-microservices-with-spring-boot-2-0-eureka-and-spring-cloud/comment-page-1/#comment-574

https://github.com/piomin/sample-spring-microservices-new


