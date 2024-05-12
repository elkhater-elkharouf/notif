package com.example.userservice.Services.Projet;
import java.util.Optional;

import com.example.userservice.Entities.Mail;
import com.example.userservice.Entities.Projet;
import com.example.userservice.Entities.User;
import com.example.userservice.Repository.MailRepository;
import com.example.userservice.Repository.ProjetRepository;
import com.example.userservice.Repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class ProjetServiceImpTest {
@Autowired
private IProjetService iProjetService ;
@Autowired
    ProjetRepository projetRepository ;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MailRepository mailRepository;

    @Autowired
    private ProjetServiceImp projetService;
    @Test
    void addProjet() {
        Projet projet =new Projet();
        projet.setNameProjet("projet1");
        projet.setDateDeb(new Date());
        projet.setStatus(1);
        projet.setDateFin(new Date());

        Projet savedProjet = iProjetService.addProjet(projet);
        Assertions.assertNotNull(savedProjet.getIdProjet());
        Assertions.assertEquals("projet1", savedProjet.getNameProjet());
        Assertions.assertEquals(projet.getDateDeb(), savedProjet.getDateDeb());
        Assertions.assertEquals(projet.getDateFin(), savedProjet.getDateFin());
        Assertions.assertEquals(1, savedProjet.getStatus());



    }

    @Test
    void updateProjet() {
        // Données de test
        Projet projet = new Projet();
        projet.setIdProjet(1);
        projet.setNameProjet("Test Projet");
        projet.setDateDeb(new Date()); // Vous devrez initialiser correctement ces valeurs en fonction de votre logique métier
        projet.setStatus(1);
        projet.setDateFin(new Date());

        // Enregistrer le projet initial dans le repository
        Projet savedProjet = projetRepository.save(projet);

        // Modifier les détails du projet
        savedProjet.setNameProjet("Updated Test Projet");
        savedProjet.setStatus(2);

        // Mettre à jour le projet
        Projet updatedProjet = iProjetService.updateProjet(savedProjet);

        // Vérification
        assertEquals(savedProjet.getIdProjet(), updatedProjet.getIdProjet());
        assertEquals("Updated Test Projet", updatedProjet.getNameProjet());
        assertEquals(savedProjet.getDateDeb(), updatedProjet.getDateDeb());
        assertEquals(savedProjet.getDateFin(), updatedProjet.getDateFin());
        assertEquals(2, updatedProjet.getStatus());

        // Vérifie si la mise à jour a réussi en cherchant le projet dans le repository
        Optional<Projet> retrievedProjet = projetRepository.findById(updatedProjet.getIdProjet());
        assertTrue(retrievedProjet.isPresent());
        assertEquals("Updated Test Projet", retrievedProjet.get().getNameProjet());
    }
    @Test
    void deleteProjet() {
        // Données de test
        Projet projet = new Projet();
        projet.setNameProjet("Projet à supprimer");

        // Enregistrer le projet dans le repository
        Projet savedProjet = projetRepository.save(projet);

        // Récupérer l'ID du projet sauvegardé
        int projetId = savedProjet.getIdProjet();

        // Supprimer le projet
        iProjetService.deleteProjet(projetId);

        // Vérification
        Optional<Projet> deletedProjet = projetRepository.findById(projetId);
        if(deletedProjet ==null){
            Assertions.fail("Delation failed");
        }else {
           Assertions.assertTrue(true);
        }
    }
    @Test
    void getProjetById_ProjetExists() {
        // Créer un projet
        Projet projet = new Projet();
        projet.setNameProjet("Test Projet");
        projet.setDateDeb(new Date());
        projet.setStatus(1);
        projet.setDateFin(new Date());

        // Enregistrer le projet dans la base de données
        projetRepository.save(projet);

        // Récupérer le projet par son ID
        Projet retrievedProjet = projetService.getProjetById(projet.getIdProjet());

        // Vérifications
        assertNotNull(retrievedProjet);

    }

    @Test
    void getProjetById_ProjetNotExists() {
        // Récupérer un projet avec un ID inexistant
        Projet retrievedProjet = projetService.getProjetById(-1);

        // Vérifier si le projet récupéré est null
        assertNull(retrievedProjet);
    }
    @Test
    void getAllProjets() {
        List<Projet> retrievedProjet = iProjetService.getAllProjets();

        if (!retrievedProjet.isEmpty()) {
            System.out.println("Size of retrieved list: " + retrievedProjet.size());
            Assertions.assertEquals(retrievedProjet.size(), retrievedProjet.size());
            // Add any other necessary assertions based on your requirements
        } else {
            System.out.println("Retrieved list is empty.");
            Assertions.assertEquals(0, retrievedProjet.size());
        }
    }

//    @Test
//    @Transactional
//    void addProjetWithMailAndUsers() {
//        // Données de test
//        String projectName = "Test Projet";
//        String host = "testHost";
//        int port = 1234;
//        String username = "testUser";
//        String password = "testPassword";
//        Date startDate = new Date();
//        Date endDate = new Date();
//int user1Id=1;
//int user2Id=2 ;
//
//        // Enregistrer le mail dans la base de données
//        Mail mail = new Mail();
//        mail.setHost(host);
//        mail.setPort(port);
//        mail.setUsername(username);
//        mail.setPassword(password);
//        mailRepository.save(mail);
//
//
//        // Fetch fresh instances of User entities
//        User user1 = userRepository.findById(user1Id).orElseThrow(() -> new RuntimeException("User not found with id: " + user1Id));
//        User user2 = userRepository.findById(user2Id).orElseThrow(() -> new RuntimeException("User not found with id: " + user2Id));
//
//
//        // Créer une liste d'utilisateurs à associer au projet
//        List<Integer> userIds = List.of(user1.getIdUser(), user2.getIdUser());
//
//        // Créer un projet
//        Projet projet = new Projet();
//        projet.setNameProjet(projectName);
//        projet.setDateDeb(startDate);
//        projet.setStatus(1);
//        projet.setDateFin(endDate);
//        projet.setMail(mail);
//
//        // Exécuter la méthode à tester
//        Projet savedProjet = iProjetService.addProjetWithMailAndUsers(projet, userIds);
//
//        // Vérifications
//        assertNotNull(savedProjet.getIdProjet());
//        assertEquals(projectName, savedProjet.getNameProjet());
//        assertNotNull(savedProjet.getMail());
//        assertEquals(host, savedProjet.getMail().getHost());
//        assertEquals(port, savedProjet.getMail().getPort());
//        assertEquals(username, savedProjet.getMail().getUsername());
//        assertEquals(password, savedProjet.getMail().getPassword());
//        assertEquals(2, savedProjet.getUsers().size()); // Vérifie que les deux utilisateurs sont associés au projet
//    }



    }

