package com.example.userservice.Services.User;

import com.example.userservice.Entities.User;
import com.example.userservice.Entities.VerificationToken;

import com.example.userservice.Security.MailConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.io.File;



@Service

@Transactional
public class EmailUserService {
    private VerificationTokenService verificationTokenService;
 private JavaMailSender javaMailSender;
@Autowired
private MailConfig mailConfig ;

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
        System.out.println(user.getIdUser()+" dddddd  "+user.getEmail()+"hhhhhhhhhhhhhhhhhhhh$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        VerificationToken verificationToken = verificationTokenService.findByUser(user);

        if (verificationToken != null){
            String token = verificationToken.getToken();

            String subject = "Request for reset password";
            String senderName = "Plateforme Notification";

            String mailContent = "<p> Someone has requested to reset your password with our project .If it were not you , please ignore it otherwise please click on the link below : </p>";
            String verifyURL = "http://localhost:4200/pages/authentication/reset-password-v2?token=" + token;

            mailContent += "<h2><a href=" + verifyURL + ">Click this link to reset password</a></h2>";

            mailContent += "<p> thank you<br> the Notification App Team</p>";
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);


            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(mailContent, true);

            javaMailSender.send(message);
        }
    }


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
