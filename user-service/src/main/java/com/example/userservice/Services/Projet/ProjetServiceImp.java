package com.example.userservice.Services.Projet;

import com.example.userservice.Entities.*;
import com.example.userservice.Repository.MailRepository;
import com.example.userservice.Repository.ProjetRepository;
import com.example.userservice.Repository.UserRepository;
import com.example.userservice.Services.Mail.MailServiceImpl;
import com.example.userservice.Services.User.FirebaseNotificationService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    private FirebaseNotificationService firebaseNotificationService;
   // private static final String STATIC_FCM_TOKEN = "epC_KhE6nqOku-t7xsdKkw:APA91bFk84BwjOGu-Tow6ajHLoRhf62hYve-zoO9Sg5eLMPOGOowdbT0RWpaGX0pZ0MR5Z61LfbPz_D8Rt0VuMTzHCcWiBQO_yGlx6x3-iFbO2itdTU8m_S2LUoLN6BP1u2KYDltJ0Pp";
   @Override
   public Page<Projet> getAllProjectpaginated(int page, int size) {
       Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "idProjet"));
       return projetRepository.findAll(pageable);
   }
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
        // Fetch the project to delete
        Projet projet = projetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projet not found with id: " + id));

        // Delete associated Mail if it exists
        if (projet.getMail() != null) {
            mailRepository.delete(projet.getMail());
        }

        // Create a temporary list of users to avoid ConcurrentModificationException
        List<User> usersToRemove = new ArrayList<>(projet.getUsers());

        // Remove the projet from all associated users
        for (User user : usersToRemove) {
            user.getProjets().remove(projet);
            userRepository.save(user); // Save user to update the relationship
        }

        // Optionally, delete the users if they are not associated with any other projects
        for (User user : usersToRemove) {
            if (user.getProjets().isEmpty()) {
                userRepository.delete(user);
            }
        }

        // Finally, delete the projet
        projetRepository.delete(projet);
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

        // Envoi des notifications push aux utilisateurs
        String title = "Nouveau projet assigné";
        String body = "Vous avez été assigné à un nouveau projet : " + projet.getNameProjet();

        for (User user : affectedUsers) {
            String token = user.getFcmToken(); // Assuming User entity has an fcmToken field
            if (token != null && !token.isEmpty()) {
                firebaseNotificationService.sendNotification(token, title, body);
            }
        }


        // Envoi de l'e-mail aux utilisateurs
       // sendEmailToUsers(affectedUsers, mail);
        return savedProjet;
    }
@Override
    public Set<User> getUsersByProjetId(int projetId) {
        Projet projet = projetRepository.findById(projetId).orElse(null);
        if (projet != null) {
            return projet.getUsers();
        }
        return null; // Or throw an exception if needed
    }//    private void sendEmailToUsers(Set<User> users, Mail mail) {

    @Override
    public List<Projet> getProjectByUser(int idUser) {

            // Récupérer l'utilisateur par son id
            User user = userRepository.findById(idUser).orElse(null);
            if (user != null) {
                // Récupérer les projets de l'utilisateur
                Set<Projet> projets = user.getProjets();
                return new ArrayList<>(projets);
            }
            return null; // Ou retourner une liste vide, selon vos besoins
        }
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


