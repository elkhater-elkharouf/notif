package com.example.userservice.Services.User;

import com.example.userservice.Entities.EmailTemplate;
import com.example.userservice.Entities.GenericNotification;
import com.example.userservice.Repository.EmailTemplateRepository;
import com.example.userservice.Security.MailConfig;
import com.example.userservice.Security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import javax.mail.MessagingException;
import org.springframework.mail.javamail.MimeMessageHelper;
import javax.mail.internet.MimeMessage;
import java.io.StringWriter;
import java.util.Map;

@Slf4j
@Service
public class EmailServiceImpl implements IEmailService {
    /**
     * JavaMailSender
     */
    @Autowired
    private SecurityUtils securityUtils;
//    @Autowired
//    public JavaMailSender emailSender;
@Autowired
UserServiceImp userServiceImp ;
    @Autowired
    private EmailTemplateRepository emailRepository;
    @Value("${send.mail.from}")
    public String emailFrom;
    @Override
    public void sendEmailSpecificTemplate(GenericNotification template ) throws MessagingException {

        log.info("Start sending email");
        VelocityEngine velocityEngine = new VelocityEngine();
       // JavaMailSender emailSender = mailConfig.getJavaMailSenderForCurrentUser();
//        if (emailSender == null) {
//            // Handle case when no email configuration found for current user
//            return;
//        }
        MimeMessage mimeMessage = userServiceImp.getJavaMailSenderForCurrentUser().createMimeMessage();
       // MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

        // Créez MimeMessageHelper en mode multipart
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

// get mail template from database
        EmailTemplate email = emailRepository.findEmailTemplateByLabel(template.getLabel());
        if (email == null) {
            throw new MessagingException();
        }
        mimeMessageHelper.setFrom(emailFrom);
        mimeMessageHelper.setTo(template.getEmailTo());
        velocityEngine.init();
// Create a context to hold data
        VelocityContext context = new VelocityContext();
        for (Map.Entry<String, Object> entry : template.getAttributes().entrySet()) {
            context.put(entry.getKey(), entry.getValue());

        }
// Create a StringWriter to capture the output
        StringWriter writer = new StringWriter();
// Evaluate the template with the data

            mimeMessageHelper.setSubject(email.getObject());
            velocityEngine.evaluate(context, writer, "template", email.getContentHtml());

        mimeMessageHelper.setText(writer.toString(), true);
        // Ajouter la pièce jointe si elle est présente dans le template
        if (template.getAttachmentData() != null && template.getLabel()=="augmentation de salaire") {
            mimeMessageHelper.addAttachment("output_" + template.getLabel() + ".docx",
                    new ByteArrayResource(template.getAttachmentData()));
        }
        else if(template.getAttachmentData() != null && template.getLabel()=="aid mubarek"){
            mimeMessageHelper.addAttachment("output_" + template.getLabel()+ ".pptx",
                    new ByteArrayResource(template.getAttachmentData()));
        }
       userServiceImp.getJavaMailSenderForCurrentUser().send(mimeMessage);
    }
/////////// PRIVATE METHODS /////////////////////////////////////
}