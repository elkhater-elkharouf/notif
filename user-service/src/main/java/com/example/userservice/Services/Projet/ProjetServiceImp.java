package com.example.userservice.Services.Projet;

import com.example.userservice.Entities.*;
import com.example.userservice.Repository.MailRepository;
import com.example.userservice.Repository.ProjetRepository;
import com.example.userservice.Repository.UserRepository;
import com.example.userservice.Services.Mail.MailServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@AllArgsConstructor
public class ProjetServiceImp implements  IProjetService {
    ProjetRepository projetRepository;
    MailRepository mailRepository;
    UserRepository userRepository;
    MailServiceImpl mailService ;
    //private JavaMailSender javaMailSender;

    @Override
    public Projet addProjet(Projet Projet) {
        return projetRepository.save(Projet);
    }

    @Override
    public Projet updateProjet(Projet Projet) {
        return projetRepository.save(Projet);
    }

    @Override
    public void deleteProjet(int id) {
        projetRepository.deleteById(id);

    }

    @Override
    public Projet getProjetById(int id) {
        return projetRepository.findById(id).orElse(null);
    }

    @Override
    public List<Projet> getAllProjets() {
        return projetRepository.findAll();
    }

    @Override
    public Projet addMailAndAsseignToProject(Mail mail, int idP) {
        Optional<Projet> optionalProjet = projetRepository.findById(idP);
        if (optionalProjet.isPresent()) {
            Projet projet = optionalProjet.get();
            mail.setProjet(projet);
            projet.setMail(mail);
            mailRepository.save(mail);
            return projetRepository.save(projet);
        } else {
            throw new IllegalArgumentException("Projet not found with ID: " + idP);
        }
    }

    @Override
    public Projet addProjetWithUsers(Projet projet, Set<Integer> userIds) {
        Set<User> users = new HashSet<>();
        projet.setDateDeb(new Date());
        for (Integer userId : userIds) {
            Optional<User> optionalUser = userRepository.findById(userId);
            optionalUser.ifPresent(users::add);
        }
        projet.setUsers(users);
        return projetRepository.save(projet);
    }
    @Override
    public Projet addProjetWithMailAndUsers(Projet projet, List<Integer> userIds) {

        // Récupérer les utilisateurs à partir de leurs identifiants
        Set<User> affectedUsers = new HashSet<>();
        for (Integer userId : userIds) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            affectedUsers.add(user);
        }

        // Associer le projet aux utilisateurs
        for (User user : affectedUsers) {
            user.getProjets().add(projet);
        }

        // Associer les utilisateurs au projet
        projet.setUsers(affectedUsers);
// Récupérer l'email associé au projet
        Mail mail = projet.getMail();

// Associer le projet à l'email
        mail.setProjet(projet);

// Enregistrer l'email
        mailService.addEmail(mail);
        // Enregistrer le projet avec les utilisateurs associés
        Projet savedProjet = projetRepository.save(projet);

        // Envoi de l'e-mail aux utilisateurs
       // sendEmailToUsers(affectedUsers, mail);
        return savedProjet;
    }


//    private void sendEmailToUsers(Set<User> users, Mail mail) {
//        // Créer un message e-mail
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setSubject("Nouveau Projet: " + mail.getProjet().getNameProjet());
//        message.setText("Bonjour,\n\nUn nouveau projet a été ajouté : " + mail.getProjet().getNameProjet());
//        message.setFrom(mail.getUsername());
//
//        // Envoyer l'e-mail à chaque utilisateur
//        for (User user : users) {
//            message.setTo(user.getEmail());
//            javaMailSender.send(message);
//        }
//    }
}


