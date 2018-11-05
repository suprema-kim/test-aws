package com.example.jwt.jwtsample.email;

import lombok.Data;

@Data
public class EmailReq {
    private String from;
    private String to;
    private String subject;
    private String content;
}
