package com.xepr.core.service;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Scope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;

import lombok.Setter;

import java.util.regex.Pattern;

@Scope("prototype")
@Service
@Setter
public class EmailService {

    @Autowired
    private JavaMailSender jms;

    @Value("${spring.mail.username}")
    private String sender;

    private String recipient;

    private String subject;

    private String messageHeading;

    private String message;

    void sendEmail() throws MessagingException {
        MimeMessage m = this.jms.createMimeMessage();

        m.setFrom(new InternetAddress(this.sender));
        m.setRecipient(Message.RecipientType.TO, new InternetAddress(this.recipient));
        m.setSubject(this.subject);
        m.setContent(this.getHtml(), "text/html; charset=utf-8");

        this.jms.send(m);
    }

    private String getHtml() {
        return "<h2 style = \"color: #faf9f6\">" + this.messageHeading + "</h2>" +
                "<p style = \"color: #faf9f6\">" + this.message + "</p>";
    }

    static boolean isValidEmail(String email) {
        return Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
                .matcher(email).matches();
    }
}
