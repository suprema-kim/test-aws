package com.example.jwt.jwtsample.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class JwtTestController {
    private static final String key = "abcde";

    @Autowired
    JwtTokenBuilder jwtTokenBuilder;

    @RequestMapping(value = "/token", method = RequestMethod.GET)
    public ResponseEntity<String> getToken() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("iat", System.currentTimeMillis());
        claims.put("sub", "test");

        String token = jwtTokenBuilder.getJwtToken(claims);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    @RequestMapping(value = "/echo")
    public ResponseEntity<String> echo() {
        return new ResponseEntity<>("yahoo", HttpStatus.OK);
    }
}
