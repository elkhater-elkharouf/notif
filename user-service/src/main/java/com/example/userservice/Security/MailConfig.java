package com.example.userservice.Security;

import com.example.userservice.Entities.Mail;
import com.example.userservice.Repository.MailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.mail.javamail.*;

import java.util.*;
//
@Configuration
public class MailConfig {
    @Autowired
    private MailRepository mailConfigRepository;

@Bean
    public JavaMailSender getJavaMailSenderForCurrentUser() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        // Obtenir l'utilisateur connecté
      //  Integer currentUserId = securityUtils.getCurrentUserId();
        // Récupérer les informations de configuration depuis la base de données
        Mail mailConfigEntity = mailConfigRepository.findById(3).orElse(null);
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


