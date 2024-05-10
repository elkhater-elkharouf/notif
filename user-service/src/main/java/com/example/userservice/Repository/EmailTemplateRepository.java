package com.example.userservice.Repository;

import com.example.userservice.Entities.EmailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailTemplateRepository extends JpaRepository<EmailTemplate,Long> {
    public EmailTemplate findEmailTemplateByLabel(String label);

}
