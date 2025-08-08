package com.bugforge.BugForge.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    // This value is taken from your application.properties file
    @Value("${spring.mail.username}")
    private String fromEmailAddress;

    /**
     * Sends a simple text-based email.
     * @param to The recipient's email address.
     * @param subject The subject line of the email.
     * @param text The body of the email.
     */
    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        
        message.setFrom(fromEmailAddress);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        
        javaMailSender.send(message);
    }
}