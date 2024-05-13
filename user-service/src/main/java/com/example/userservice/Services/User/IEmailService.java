package com.example.userservice.Services.User;

import com.example.userservice.Entities.GenericNotification;

import javax.mail.MessagingException;

public interface IEmailService {

    public void sendEmailSpecificTemplate(GenericNotification template) throws MessagingException;
}
