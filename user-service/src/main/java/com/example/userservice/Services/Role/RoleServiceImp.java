package com.example.userservice.Services.Role;


import com.example.userservice.Entities.Privilege;
import com.example.userservice.Entities.Role;
import com.example.userservice.Repository.PrivilegeRepository;
import com.example.userservice.Repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class RoleServiceImp implements IRoleService {

RoleRepository roleRepository;
PrivilegeRepository privilegeRepository;

    @Override
    public Role addRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public Role updateRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public void deleteRole(int id) {
        roleRepository.deleteById(id);
    }

    @Override
    public Role getRoleById(int id) {
        return roleRepository.findById(id).orElse(null);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    @Transactional
    public Role AddRoleWithPrivilege(Role role) {
        // Assurez-vous que la liste de privilèges du rôle n'est pas null
        if (role.getPrivileges() == null) {
            role.setPrivileges(new HashSet<>());
        }

        // Parcourir les privilèges et les associer au rôle
        for (Privilege privilege : role.getPrivileges()) {
            // Recherche du privilège dans la base de données
            Privilege retrievedPrivilege = privilegeRepository.findById(privilege.getIdPrivilege())
                    .orElseThrow(() -> new RuntimeException("Privilege not found with id: " + privilege.getIdPrivilege()));

            // Associer le rôle au privilège
            retrievedPrivilege.getRoles().add(role);
            // Sauvegarder le privilège modifié
            privilegeRepository.save(retrievedPrivilege);
        }

        // Sauvegarder le rôle avec les privilèges associés
        return roleRepository.save(role);
    }


    @Override
    public Boolean AffectRoleToPrivilege(int idRole , int idPrivilege){
        Optional<Role> optionalRole = roleRepository.findById(idRole);
        Optional<Privilege> optionalPrivilege = privilegeRepository.findById(idPrivilege);

        if (optionalRole.isPresent() && optionalPrivilege.isPresent()) {
            Role role = optionalRole.get();
            Privilege privilege = optionalPrivilege.get();

            // Ajouter le privilège au rôle et le rôle au privilège
            role.getPrivileges().add(privilege);
            privilege.getRoles().add(role);

            // Enregistrer les modifications dans la base de données
            roleRepository.save(role);

            return true;
        } else {
            return false; // Si le rôle ou le privilège n'existe pas
        }
    }


}
