package com.example.userservice.Repository;

import com.example.userservice.Entities.Mail;
import com.example.userservice.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface MailRepository extends JpaRepository<Mail , Integer> {
    @Query("SELECT m FROM Mail m JOIN m.projet p JOIN p.users u WHERE u.idUser = :userId")
    Mail findByUserId(@Param("userId") Integer userId);
   // Mail findByUsername(String username);
    List<Mail> findByUsername(String username);
}
