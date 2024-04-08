package com.example.userservice.Services.User;

import com.example.userservice.Entities.User;
import com.example.userservice.Entities.VerificationToken;
import com.example.userservice.Repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;


@Service

@Transactional
public class EmailUserService {
    private VerificationTokenService verificationTokenService;
    private JavaMailSender javaMailSender;
    UserRepository userRepository ;


  //  private String host=

    public EmailUserService(VerificationTokenService verificationTokenService, JavaMailSender javaMailSender) {
        this.verificationTokenService = verificationTokenService;
        this.javaMailSender = javaMailSender;
    }

    public void sendHtmlMail(User user) throws MessagingException {
        VerificationToken verificationToken = verificationTokenService.findByUser(user);
        if (verificationToken != null){
            String token = verificationToken.getToken();
            String body = "<p>  Click on the link to verify your email address" +" http://localhost:9999/USER-SERVICE/activation?token="+token;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("katerkarouf26@gmail.com");
            message.setTo(user.getEmail());
            message.setText(body);
            message.setSubject("email address verification");
            javaMailSender.send(message);
            System.out.println("Mail send ...");
            /*
            Context context = new Context();
            context.setVariable("title","Verify your email address");
            context.setVariable("link","http://localhost:8085/activation?token="+token);

            //send the verification email
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,true);
            helper.setTo(user.getEmail());
            helper.setSubject("email address verification");

            helper.setText("Verify your email address \n" +
                    "Click on the link to verify your email address http://localhost:8085/activation?token="+token  );

            javaMailSender.send(message);

            */

        }

    }


    public void resetPasswordMail(User user) throws MessagingException{
       // System.out.println(user.getIdUser()+" dddddd  "+user.getEmail()+"hhhhhhhhhhhhhhhhhhhh$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        VerificationToken verificationToken = verificationTokenService.findByUser(user);

        if (verificationToken != null){
            String token = verificationToken.getToken();

        String subject = "Request for reset password";
        String senderName = "Platform de notification";

        String mailContent = "<p> Someone has requested to reset your password with our project .If it were not you , please ignore it otherwise please click on the link below : </p>";
        String verifyURL = "http://localhost:9999/USER-SERVICE/password-reset?token=" + token;

        mailContent += "<h2><a href=" + verifyURL + ">Click this link to reset password</a></h2>";

        mailContent += "<p> thank you<br> the platform of notification App Team</p>";
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);

           // helper.setFrom("katerkarouf26@gmail.com");
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(mailContent, true);

            javaMailSender.send(message);
        }
    }
/*
    public void sendEmail(String recipient, String subject, String senderName, String mailContent, MultipartFile attachment) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(recipient);
        helper.setSubject(subject);
        helper.setText(mailContent, true);
        helper.setFrom(senderName);
        // Ajout de la pièce jointe
       FileSystemResource file = new FileSystemResource(new File(String.valueOf(attachment)));
       helper.addAttachment(file.getFilename(), file);
        // Ajout de la pièce jointe
      //  ByteArrayResource resource = new ByteArrayResource(filePath.getBytes());
        //helper.addAttachment(filePath.getOriginalFilename(), resource);
       /* if (attachment != null) {
            helper.addAttachment(attachment.getOriginalFilename(), attachment);
        }*//*
        javaMailSender.send(message);
    }
*/

    public void sendMailWithAttchment(String toEmail,
                                     String body,
                                     String subject,
                                     String attachment) throws MessagingException{
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(body, true);
        helper.setFrom("katerkarouf26@gmail.com");
        // Ajout de la pièce jointe
        FileSystemResource file = new FileSystemResource(new File(String.valueOf(attachment)));
        helper.addAttachment(file.getFilename(), file);
        javaMailSender.send(message);
    }

}
