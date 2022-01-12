package com.oopsmails.spring.cloud.microservices.authserver;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SpringCloudMicroservicesAuthApplicationTest {
    @Test
    public void whenLoadApplication_thenSuccess() {
        String toBeEncodedStr = "my_secret";
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String result = bCryptPasswordEncoder.encode(toBeEncodedStr);
        System.out.println("+++++++++++++++++++++++ Encoded: " + toBeEncodedStr + " as: " + result);
        assertTrue(result != null);
    }
}
