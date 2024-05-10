package com.example.userservice.Services.Mail;

import com.example.userservice.Entities.Mail;

import java.util.List;

public interface IEmailService {
    public Mail addEmail(Mail mail) ;
    public List<Mail> AllMail() ;
}
