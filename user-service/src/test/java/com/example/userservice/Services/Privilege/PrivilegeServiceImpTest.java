package com.example.userservice.Services.Privilege;

import com.example.userservice.Entities.Privilege;
import com.example.userservice.Repository.PrivilegeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class PrivilegeServiceImpTest {
//
//   @Autowired
//    private PrivilegeServiceImp privilegeService;
//
//    @Autowired
//    private PrivilegeRepository privilegeRepository;
//
//    @Test
//    void addPrivilege() {
//        // Créer un nouvel objet Privilege
//        Privilege privilege = new Privilege();
//        privilege.setPrivilegeName("TEST_PRIVILEGE");
//
//        // Appeler la méthode à tester
//        Privilege savedPrivilege = privilegeService.addPrivilege(privilege);
//
//        // Récupérer le Privilege à partir de la base de données pour vérifier s'il a été correctement enregistré
//        Privilege retrievedPrivilege = privilegeRepository.findById(savedPrivilege.getIdPrivilege()).orElse(null);
//
//        // Vérifier que le Privilege enregistré n'est pas null
//        assertNotNull(retrievedPrivilege);
//        // Vérifier que le nom du Privilege enregistré correspond au nom défini
//        assertEquals("TEST_PRIVILEGE", retrievedPrivilege.getPrivilegeName());
//    }
//    @Test
//    void updatePrivilege() {
//        // Données de test
//        Privilege privilege = new Privilege();
//        privilege.setIdPrivilege(1);
//        privilege.setPrivilegeName("Test Privilege");
//
//        // Enregistrer le privilège initial dans le repository
//        Privilege savedPrivilege = privilegeRepository.save(privilege);
//
//        // Modifier les détails du privilège
//        savedPrivilege.setPrivilegeName("Updated Test Privilege");
//
//        // Mettre à jour le privilège
//        Privilege updatedPrivilege = privilegeService.updatePrivilege(savedPrivilege);
//
//        // Vérification
//        assertEquals(savedPrivilege.getIdPrivilege(), updatedPrivilege.getIdPrivilege());
//        assertEquals("Updated Test Privilege", updatedPrivilege.getPrivilegeName());
//
//        // Vérifie si la mise à jour a réussi en cherchant le privilège dans le repository
//        Optional<Privilege> retrievedPrivilege = privilegeRepository.findById(updatedPrivilege.getIdPrivilege());
//        assertTrue(retrievedPrivilege.isPresent());
//        assertEquals("Updated Test Privilege", retrievedPrivilege.get().getPrivilegeName());
//    }

//@Test
//void deletePrivilege() {
//    // Données de test
//    Privilege privilege = new Privilege();
//    privilege.setPrivilegeName("Privilege à supprimer");
//
//    // Enregistrer le privilège dans le repository
//    Privilege savedPrivilege = privilegeRepository.save(privilege);
//
//    // Récupérer l'ID du privilège sauvegardé
//    int privilegeId = savedPrivilege.getIdPrivilege();
//
//    // Supprimer le privilège
//    privilegeService.deletePrivilege(privilegeId);
//
//    // Vérification
//    Optional<Privilege> deletedPrivilege = privilegeRepository.findById(privilegeId);
//    assertFalse(deletedPrivilege.isPresent(), "La suppression du privilège a échoué");
//}

//    @Test
//    void getPrivilegeById_PrivilegeExists() {
//        // Créer un privilège
//        Privilege privilege = new Privilege();
//        privilege.setPrivilegeName("Test Privilege");
//
//        // Enregistrer le privilège dans la base de données
//        Privilege savedPrivilege = privilegeRepository.save(privilege);
//
//        // Récupérer le privilège par son ID
//        Privilege retrievedPrivilege = privilegeService.getPrivilegeById(savedPrivilege.getIdPrivilege());
//
//        // Vérifications
//        assertNotNull(retrievedPrivilege, "Le privilège récupéré ne doit pas être null");
//        assertEquals(savedPrivilege.getIdPrivilege(), retrievedPrivilege.getIdPrivilege(), "Les ID des privilèges doivent être identiques");
//        assertEquals(savedPrivilege.getPrivilegeName(), retrievedPrivilege.getPrivilegeName(), "Les noms des privilèges doivent être identiques");
//    }
//    @Test
//    void getPrivilegeById_PrivilegeNotExists() {
//        // Récupérer un projet avec un ID inexistant
//        Privilege retrievedPrivilege = privilegeService.getPrivilegeById(-1);
//
//        // Vérifier si le projet récupéré est null
//        assertNull(retrievedPrivilege, "Le Privilege récupéré devrait être null car le Privilege n'existe pas");
//    }

//    @Test
//    void getAllPrivileges() {
//        List<Privilege> retrievedPrivileges = privilegeService.getAllPrivileges();
//
//        if (!retrievedPrivileges.isEmpty()) {
//            System.out.println("Size of retrieved list: " + retrievedPrivileges.size());
//            Assertions.assertEquals(retrievedPrivileges.size(), retrievedPrivileges.size());
//            // Ajoutez d'autres assertions nécessaires en fonction de vos besoins
//        } else {
//            System.out.println("Retrieved list is empty.");
//            Assertions.assertEquals(0, retrievedPrivileges.size());
//        }
//    }
//
//    @Test
//    void addAndAssignPrivilegeToRole() {
//    }
//
//    @Test
//    void assignPrivilegeToRole() {
//    }
}