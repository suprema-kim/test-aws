package com.example.jwt.jwtsample.email;

import com.amazonaws.AmazonClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailSendController {
    private static final Logger log = LoggerFactory.getLogger(EmailSendController.class);

    @Autowired
    EmailSender sender;

    @RequestMapping(value = "/email", method = RequestMethod.POST)
    public ResponseEntity<String> sendEmail(@RequestBody EmailReq body) {
        log.info("request: POST /email");
        log.info("body: " + body.toString());

        SenderDto dto = SenderDto.builder()
                .from(body.getFrom())
                .to(body.getTo())
                .subject(body.getSubject())
                .content(body.getContent())
                .build();

        log.info("Sender DTO: " + dto.toString());
        try {
            sender.send(dto);
        } catch (AmazonClientException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }

        return new ResponseEntity<String>(HttpStatus.OK);
    }
}
