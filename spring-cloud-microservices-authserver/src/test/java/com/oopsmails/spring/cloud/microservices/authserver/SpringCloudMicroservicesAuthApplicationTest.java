package com.oopsmails.spring.cloud.microservices.authserver;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
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
