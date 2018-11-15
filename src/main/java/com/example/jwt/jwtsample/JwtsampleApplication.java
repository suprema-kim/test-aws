package com.example.jwt.jwtsample;

import com.example.jwt.jwtsample.properties.CustomConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class JwtsampleApplication implements CommandLineRunner {
    @Autowired
    private CustomConfigProperties customConfigProperties;

    public static void main(String[] args) {
        SpringApplication.run(JwtsampleApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("custom.age: " + customConfigProperties.getAge());
        System.out.println("custom.company.name: " + customConfigProperties.getCompany().getName());
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
