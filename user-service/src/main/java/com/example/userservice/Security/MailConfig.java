package com.example.userservice.Security;

import com.example.userservice.Entities.Mail;
import com.example.userservice.Repository.MailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.mail.*;
import org.springframework.mail.javamail.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.activation.FileTypeMap;
import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.util.*;

@Configuration
public class MailConfig {
    @Autowired
    private MailRepository mailConfigRepository;
//   @Autowired
//private SecurityUtils securityUtils;
@Bean
    public JavaMailSender getJavaMailSenderForCurrentUser() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        // Obtenir l'utilisateur connecté
      //  Integer currentUserId = securityUtils.getCurrentUserId();
        // Récupérer les informations de configuration depuis la base de données
        Mail mailConfigEntity = mailConfigRepository.findById(1).orElse(null);
     //  Mail mailConfigEntity = mailConfigRepository.findByUserId(currentUserId);
      //  Long userId = null;
        // Convertir l'ID de l'utilisateur en Long s'il n'est pas nul
//        if (currentUserId != null) {
//            userId = currentUserId;
//        }
        // Récupérer les informations de configuration depuis la base de données
//        Mail mailConfigEntity = null;
//        if (currentUserId != null) {
//            mailConfigEntity = mailConfigRepository.findByUserId(currentUserId);
//        }
//        if (userId != null) {
//            mailConfigEntity = mailConfigRepository.findByUserId(userId);
//        }
        if (mailConfigEntity != null) {
            mailSender.setHost(mailConfigEntity.getHost());
            mailSender.setPort(mailConfigEntity.getPort());
            mailSender.setUsername(mailConfigEntity.getUsername());
            mailSender.setPassword(mailConfigEntity.getPassword());

            Properties props = mailSender.getJavaMailProperties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
        } else {
            // Gérer le cas où aucune configuration de messagerie n'est trouvée dans la base de données
            // Vous pouvez également utiliser des valeurs par défaut ou lancer une exception
            System.out.println("mayemechich");

        }

        return mailSender;
    }

}
/*    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("katerkarouf26@gmail.com");
        mailSender.setPassword("ocgo wupl fprn cjoq");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        return mailSender;
    }*/

/*
    @Value("${email.host}")
    private String host;

    @Value("${email.from}")
    private String from;

    @Value("${email.subject}")
    private String subject;

    @Value("${email.port}")
    private int port ;

    @Bean
    public JavaMailSender javaMailService() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(host);
        javaMailSender.setPort(port);

        return javaMailSender;
    }

    @Bean
    public SimpleMailMessage simpleMailMessage() {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(from);
        simpleMailMessage.setSubject(subject);
        return simpleMailMessage;
    }*/

