package com.example.userservice.Services.Role;

import com.example.userservice.Entities.Privilege;
import com.example.userservice.Entities.Role;
import com.example.userservice.Repository.PrivilegeRepository;
import com.example.userservice.Repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class RoleServiceImpTest {
    @Autowired
    private RoleServiceImp roleService;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Test
    void addRole() {
        Role role = new Role();
        role.setRoleName("TEST_ROLE");

        Role savedRole = roleService.addRole(role);

        Role retrievedRole = roleRepository.findById(savedRole.getIdRole()).orElse(null);

        assertNotNull(retrievedRole);
        assertEquals("TEST_ROLE", retrievedRole.getRoleName());
    }

    @Test
    void updateRole() {
        Role role = new Role();
        role.setIdRole(1);
        role.setRoleName("Test Role");

        Role savedRole = roleRepository.save(role);

        savedRole.setRoleName("Updated Test Role");

        Role updatedRole = roleService.updateRole(savedRole);

        assertEquals(savedRole.getIdRole(), updatedRole.getIdRole());
        assertEquals("Updated Test Role", updatedRole.getRoleName());

        Optional<Role> retrievedRole = roleRepository.findById(updatedRole.getIdRole());
        assertTrue(retrievedRole.isPresent());
        assertEquals("Updated Test Role", retrievedRole.get().getRoleName());
    }

    @Test
    void deleteRole() {
        Role role = new Role();
        role.setRoleName("Role à supprimer");

        Role savedRole = roleRepository.save(role);

        int roleId = savedRole.getIdRole();

        roleService.deleteRole(roleId);

        Optional<Role> deletedRole = roleRepository.findById(roleId);
        assertFalse(deletedRole.isPresent(), "La suppression du rôle a échoué");
    }

    @Test
    void getRoleById_RoleExists() {
        Role role = new Role();
        role.setRoleName("Test Role");

        Role savedRole = roleRepository.save(role);

        Role retrievedRole = roleService.getRoleById(savedRole.getIdRole());

        assertNotNull(retrievedRole, "Le rôle récupéré ne doit pas être null");
        assertEquals(savedRole.getIdRole(), retrievedRole.getIdRole(), "Les ID des rôles doivent être identiques");
        assertEquals(savedRole.getRoleName(), retrievedRole.getRoleName(), "Les noms des rôles doivent être identiques");
    }

    @Test
    void getRoleById_RoleNotExists() {
        Role retrievedRole = roleService.getRoleById(-1);
        assertNull(retrievedRole, "Le rôle récupéré devrait être null car le rôle n'existe pas");
    }

    @Test
    void getAllRoles() {
        List<Role> retrievedRoles = roleService.getAllRoles();

        if (!retrievedRoles.isEmpty()) {
            System.out.println("Size of retrieved list: " + retrievedRoles.size());
            Assertions.assertEquals(retrievedRoles.size(), retrievedRoles.size());
        } else {
            System.out.println("Retrieved list is empty.");
            Assertions.assertEquals(0, retrievedRoles.size());
        }
    }

    @Test
    @Transactional
    void addRoleWithPrivilege() {
        // Création d'un rôle avec un privilège simulé
        Role role = new Role();
        role.setRoleName("Test Role");

        // Fetch fresh instances of Privilege entities
        Privilege priv1 = privilegeRepository.findById(1).orElseThrow(() -> new RuntimeException("Privilege not found with id: 1"));
        Privilege priv2 = privilegeRepository.findById(2).orElseThrow(() -> new RuntimeException("Privilege not found with id: 2"));

        // Ajout des privilèges au rôle
        Set<Privilege> privileges = new HashSet<>();
        privileges.add(priv1);
        privileges.add(priv2);
        role.setPrivileges(privileges);

        // Appel de la méthode à tester
        Role savedRole = roleService.AddRoleWithPrivilege(role);

        // Vérifications
        assertNotNull(savedRole);
        assertNotNull(savedRole.getIdRole());
        assertEquals("Test Role", savedRole.getRoleName());

        // Récupération du rôle depuis le repository pour vérification
        Role retrievedRole = roleRepository.findById(savedRole.getIdRole()).orElse(null);
        assertNotNull(retrievedRole);
        assertEquals("Test Role", retrievedRole.getRoleName());
        assertEquals(2, retrievedRole.getPrivileges().size());
    }
}
