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


================================================


================================================


================================================



==> thanks, part 1
https://piotrminkowski.wordpress.com/2018/04/26/quick-guide-to-microservices-with-spring-boot-2-0-eureka-and-spring-cloud/comment-page-1/#comment-574

https://github.com/piomin/sample-spring-microservices-new



