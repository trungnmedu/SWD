package com.swd.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Date;

@Service
public class EmailService {
    @Value("${spring.mail.username}")
    private String senderEmail;
    JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    //Send text email
    public void sendSimpleMessage(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(new InternetAddress(senderEmail, "Playground Basketball").toUnicodeString());
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            new Thread(() -> {
                mailSender.send(message);
            }).start();
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
            unsupportedEncodingException.printStackTrace();
        }
    }

    //Send email with attaches file.
    public void sendMessageWithAttachment(String to, String subject, String text, String pathToAttachment) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(senderEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);

        File file = new File(pathToAttachment);
        helper.addAttachment(file.getName(), file);
        new Thread(() -> {
            System.out.println("Thread " + Thread.currentThread().getId() + " start send attachment email at: " + new Date(System.currentTimeMillis()));
            mailSender.send(message);
            System.out.println("Thread " + Thread.currentThread().getId() + " finished send attachment email at: " + new Date(System.currentTimeMillis()));
        }).start();
    }

    public void sendHtmlTemplateMessage(String to, String subject, String htmlText) {
        try {
            MimeMessage message = mailSender.createMimeMessage();

            message.setFrom(new InternetAddress(senderEmail, "Playground Basketball"));
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            message.setContent(htmlText, "text/html;charset=UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);

            new Thread(() -> {
                mailSender.send(message);
            }).start();
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
