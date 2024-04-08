package com.example.userservice.Repository;

import com.example.userservice.Entities.Mail;
import org.springframework.data.jpa.repository.JpaRepository;



public interface MailRepository extends JpaRepository<Mail , Integer> {

}
