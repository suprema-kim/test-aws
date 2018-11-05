package com.example.jwt.jwtsample.email;

import com.amazonaws.services.simpleemail.model.*;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class SenderDto {
    private String from;
    private String to;
    private String subject;
    private String content;

    public SendEmailRequest toSendRequestDto() {
        Destination destination = new Destination()
                .withToAddresses(this.to);

        Message message = new Message()
                .withSubject(createContent(this.subject))
                .withBody(new Body().withHtml(createContent(this.content)));

        return new SendEmailRequest()
                .withSource(this.from)
                .withDestination(destination)
                .withMessage(message);
    }

    private Content createContent(String text) {
        return new Content()
                .withCharset("UTF-8")
                .withData(text);
    }
}
