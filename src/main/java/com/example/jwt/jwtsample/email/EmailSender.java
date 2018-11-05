package com.example.jwt.jwtsample.email;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmailSender {
    private static final Logger log = LoggerFactory.getLogger(EmailSender.class);

    public void send(SenderDto senderDto) {
        log.info("Attempting to send an email through SES.");

        AmazonSimpleEmailService client = null;
        try {
            client = AmazonSimpleEmailServiceClientBuilder.standard()
                    .withCredentials(InstanceProfileCredentialsProvider.getInstance())
                    .withRegion("us-west-2")
                    .build();
        } catch (AmazonClientException e) {
            log.error("Amazon SES client exception.");
            throw new AmazonClientException(e.getMessage(), e);
        }

        client.sendEmail(senderDto.toSendRequestDto());
        log.info("Email sent!");
    }
}
