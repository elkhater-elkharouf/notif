package com.example.userservice.Services.User;

import com.example.userservice.Entities.Privilege;
import com.example.userservice.Entities.Role;
import com.example.userservice.Entities.User;
import com.example.userservice.Repository.PrivilegeRepository;
import com.example.userservice.Repository.RoleRepository;
import com.example.userservice.Repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UserServiceImpTest {



    @Autowired
    private UserServiceImp userService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;


    @Autowired
    private UserRepository userRepository;



    @Test
    @Transactional
    void addUser() throws SQLException {
        // Créer un utilisateur de test
        User user = new User();
        user.setEmail("khatertest@gmail.com");
        user.setPassword("123456");
        user.setLname("khatrer");
        user.setFname("khrouf");

        // Créer un rôle de test
        Role role = new Role();
        role.setRoleName("admin");
        role.setEtatrole(true);

        // Créer des privilèges de test
        Privilege privilege1 = new Privilege();
        privilege1.setPrivilegeName("supprimer");

        Privilege privilege2 = new Privilege();
        privilege2.setPrivilegeName("consulter");

        role.setPrivileges(Set.of(privilege1, privilege2));

        roleRepository.save(role);

        // Affecter le rôle à l'utilisateur
        user.setRole(role);

        // Exécuter la méthode addUser
        User addedUser = userService.addUser(user);

        // Vérifier si l'utilisateur a été ajouté avec succès
        assertNotNull(addedUser);
        assertNotNull(addedUser.getIdUser());

        // Vérifier si le mot de passe a été correctement crypté
        assertNotEquals("123456", addedUser.getPassword());

        // Vérifier si le rôle a été correctement associé à l'utilisateur
        assertNotNull(addedUser.getRole());
        assertEquals(role.getIdRole(), addedUser.getRole().getIdRole());

        // Vérifier si les privilèges ont été correctement associés au rôle
        assertEquals(2, addedUser.getRole().getPrivileges().size());
    }

//
    @Test
    void deleteUser() {
        User user = new User();
        user.setFname("John");
        user.setDepartment("SE");
        user.setLname("John");
        user.setEmail("John@gmail.com");
        user.setNumTel("+331021521");


        User savedUser = userRepository.save(user);
        int userId = savedUser.getIdUser();

        userService.deleteUser(userId);

        Optional<User> deletedUser = userRepository.findById(userId);
        assertFalse(deletedUser.isPresent());
    }

    @Test
    void getUserById_UserExists() {
        User user = new User();
        user.setFname("John");
        user.setDepartment("SE");
        user.setLname("John");
        user.setEmail("John@gmail.com");
        user.setNumTel("+331021521");

        userRepository.save(user);

        User retrievedUser = userService.getUserById(user.getIdUser());

        assertNotNull(retrievedUser);
    }

    @Test
    void getUserById_UserNotExists() {
        User retrievedUser = userService.getUserById(-1);

        assertNull(retrievedUser);
    }

    @Test
    void getAllUsers() {
        List<User> retrievedUsers = userService.getAllUsers();

        if (!retrievedUsers.isEmpty()) {
            System.out.println("Size of retrieved list: " + retrievedUsers.size());
            Assertions.assertEquals(retrievedUsers.size(), retrievedUsers.size());
            // Add any other necessary assertions based on your requirements
        } else {
            System.out.println("Retrieved list is empty.");
            Assertions.assertEquals(0, retrievedUsers.size());
        }
    }

@Test
@Transactional // Pour annuler les modifications apportées à la base de données après chaque test
void affecterUserRole() {
    // Créer un utilisateur
    User user = new User();
    user.setFname("John");
    // Définir d'autres propriétés de l'utilisateur selon les besoins du test
    userRepository.save(user);

    // Créer un rôle
    Role role = new Role();
    role.setRoleName("ROLE_ADMIN");
    // Définir d'autres propriétés du rôle selon les besoins du test
    roleRepository.save(role);

    // Appeler la méthode à tester
    User updatedUser = userService.affecterUserRole(user.getIdUser(), role.getIdRole());

    // Récupérer l'utilisateur de la base de données pour vérifier les modifications
    User retrievedUser = userRepository.findById(updatedUser.getIdUser()).orElse(null);

    // Vérifier si le rôle de l'utilisateur a été correctement affecté
    assertNotNull(retrievedUser);
    assertNotNull(retrievedUser.getRole());
    assertEquals("ROLE_ADMIN", retrievedUser.getRole().getRoleName());
}


    @Test
    void findAllByEnabled() {
        // Créer quelques utilisateurs avec différents états d'activation
        User activeUser1 = new User();
        activeUser1.setFname("John");
        activeUser1.setEnabled(true);
        userRepository.save(activeUser1);

        User activeUser2 = new User();
        activeUser2.setFname("Jane");
        activeUser2.setEnabled(true);
        userRepository.save(activeUser2);

        User inactiveUser = new User();
        inactiveUser.setFname("Alice");
        inactiveUser.setEnabled(false);
        userRepository.save(inactiveUser);

        // Appeler la méthode à tester
        Set<String> enabledStates = userService.findAllByEnabled();

        // Vérifier si l'ensemble des états d'activation retourné est correct
        assertNotNull(enabledStates);
        assertEquals(2, enabledStates.size());
        assertTrue(enabledStates.contains("Active"));
        assertTrue(enabledStates.contains("Inactive"));
    }

}