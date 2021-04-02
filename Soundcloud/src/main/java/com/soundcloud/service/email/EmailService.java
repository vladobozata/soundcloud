package com.soundcloud.service.email;

import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    @Async
    public void send(String mail, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mail);
        message.setSubject("Please verify your registration:<br>");
        String verifyURL = "localhost:7878/verify/" + token;
//        String content = "<br>Please click the link below to verify your registration:<br>"
//                +"<a href=\"" + verifyURL +"\">Click here</a> Thanks!";
        String content = "<p>Please follow <a href=\"http://" + verifyURL + " >this link</a>.</p>";
        message.setText(content);
        this.mailSender.send(message);
    }
}