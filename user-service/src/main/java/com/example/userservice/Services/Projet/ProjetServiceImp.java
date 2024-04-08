package com.example.userservice.Services.Projet;

import com.example.userservice.Entities.Mail;
import com.example.userservice.Entities.Projet;
import com.example.userservice.Entities.User;
import com.example.userservice.Repository.MailRepository;
import com.example.userservice.Repository.ProjetRepository;
import com.example.userservice.Repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@AllArgsConstructor
public class ProjetServiceImp implements  IProjetService {
    ProjetRepository projetRepository;
    MailRepository mailRepository;
    UserRepository userRepository;

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

}


