package com.example.jwt.jwtsample.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class JwtTestController {
    private static final String key = "abcde";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JwtTokenBuilder jwtTokenBuilder;

    @RequestMapping(value = "/token", method = RequestMethod.GET)
    public ResponseEntity<String> getToken() {
        log.info("/token called...");

        Map<String, Object> claims = new HashMap<>();
        claims.put("iat", System.currentTimeMillis());
        claims.put("sub", "test");

        String token = jwtTokenBuilder.getJwtToken(claims);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    @RequestMapping(value = "/echo")
    public ResponseEntity<String> echo() {
        log.info("/echo called...");

        return new ResponseEntity<>("yahoo", HttpStatus.OK);
    }

    @RequestMapping(value = "/call")
    public ResponseEntity<String> callOther() {
        log.info("AAAAAA : call server2");
        String baseUrl = "http://172.31.18.21";
        String res = restTemplate.getForObject(baseUrl + ":9000/call", String.class);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
