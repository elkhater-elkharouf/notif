package com.example.userservice.Services.Projet;

import com.example.userservice.Entities.Projet;
import com.example.userservice.Repository.ProjetRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Date;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class ProjetServiceImpTest {
@Autowired
private IProjetService iProjetService ;
@Autowired
    ProjetRepository projetRepository ;
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
//
//    @Test
//    void updateProjet() {
//    }
//
//    @Test
//    void deleteProjet() {
//    }
//
//    @Test
//    void getProjetById() {
//    }
//
//    @Test
//    void getAllProjets() {
//    }
//
//    @Test
//    void addMailAndAsseignToProject() {
//    }
//
//    @Test
//    void addProjetWithUsers() {
//    }
//
//    @Test
//    void addProjetWithMailAndUsers() {
//    }
}