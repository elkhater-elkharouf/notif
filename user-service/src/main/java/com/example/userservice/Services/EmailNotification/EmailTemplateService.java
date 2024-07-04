package com.example.userservice.Services.EmailNotification;

import com.example.userservice.Entities.EmailTemplate;
import com.example.userservice.Repository.EmailTemplateRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailTemplateService implements IEmailTemplateService {
    EmailTemplateRepository emailTemplateRepository ;
    @Override
    public EmailTemplate addTemplate(EmailTemplate emailTemplate) {
        return emailTemplateRepository.save(emailTemplate);
    }
}
