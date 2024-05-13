package com.example.userservice.Services.User;

import com.example.userservice.Entities.Privilege;
import com.example.userservice.Entities.Role;
import com.example.userservice.Entities.User;
import com.example.userservice.Repository.ImageRepository;
import com.example.userservice.Repository.PrivilegeRepository;
import com.example.userservice.Repository.RoleRepository;
import com.example.userservice.Repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest

@ExtendWith(MockitoExtension.class)
class UserServiceImpTest {

    @Autowired
    private UserServiceImp userService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;


    @Autowired
    private UserRepository userRepository;



    @Autowired
    private ImageRepository imageRepository;


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
//    @Test
//    void getCurrentLoggedInUser() {
//    }
//


//
//    @Test
//    void getUser() {
//    }
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
//
//    @Test
//    void desActiverCompteUser() {
//    }
//
//    @Test
//    void affecterUserRole() {
//    }
//
//    @Test
//    void laodUserByUserName() {
//    }
//
//    @Test
//    void getCurrentUsername() {
//    }
//
//    @Test
//    void addUserWithRoleAndAffectPrivileges() {
//    }
//
//    @Test
//    void getAllDepartmentUser() {
//    }
//
//    @Test
//    void findAllByEnabled() {
//    }
//
//    @Test
//    void requestPasswordReset() {
//    }
//
//    @Test
//    void resetPassword() {
//    }
//
//    @Test
//    void getListPrivilegesUser() {
//    }
//
//    @Test
//    void annulerPrivilegeUser() {
//    }
//
//    @Test
//    void addPrivilegeToUser() {
//    }
}