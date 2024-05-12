package com.example.userservice.Services.User;

import com.example.userservice.Entities.Privilege;
import com.example.userservice.Entities.Role;
import com.example.userservice.Entities.User;
import com.example.userservice.Repository.PrivilegeRepository;
import com.example.userservice.Repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
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
//    @Test
//    void updateUser() {
//    }
//
//    @Test
//    void deleteUser() {
//    }
//
//    @Test
//    void getUserById() {
//    }
//
//    @Test
//    void getAllUsers() {
//    }
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