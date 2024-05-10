package com.example.userservice.Services.Mail;

import com.example.userservice.Entities.Mail;
import com.example.userservice.Repository.MailRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MailServiceImpl implements IEmailService{
    MailRepository mailRepository ;
    @Override
    public Mail addEmail(Mail mail) {
        return mailRepository.save(mail);
    }

    @Override
    public List<Mail> AllMail() {
        return mailRepository.findAll();
    }
}
