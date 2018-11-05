package com.example.jwt.jwtsample;

import com.example.jwt.jwtsample.jwt.JwtTokenBuilder;
import io.jsonwebtoken.Claims;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JwtsampleApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Test
    public void jwtTest() {
        JwtTokenBuilder jwtTokenBuilder = new JwtTokenBuilder();
        Map<String, Object> payload = new HashMap<>();

        payload.put("time", System.currentTimeMillis());
        payload.put("temp", "ttt");

        String token = jwtTokenBuilder.getJwtToken(payload);
        System.out.println("token: " + token);

        Claims claims = jwtTokenBuilder.getJwtPayload(token);
        System.out.println("claims: " + claims);

        assert true;
    }
}
