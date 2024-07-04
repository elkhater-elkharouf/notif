package com.example.userservice.Services.EmailNotification;

import com.example.userservice.Entities.EmailTemplate;
import com.example.userservice.Entities.User;

import java.sql.SQLException;

public interface IEmailTemplateService {
    public EmailTemplate addTemplate(EmailTemplate emailTemplate);

}
