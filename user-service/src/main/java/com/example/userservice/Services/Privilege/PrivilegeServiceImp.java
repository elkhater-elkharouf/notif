package com.example.userservice.Services.Privilege;



import com.example.userservice.Entities.Privilege;
import com.example.userservice.Entities.Role;
import com.example.userservice.Repository.PrivilegeRepository;
import com.example.userservice.Repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;

@Service
@AllArgsConstructor
public class PrivilegeServiceImp implements IPrivilegeService {

PrivilegeRepository privilegeRepository;
RoleRepository roleRepository;

    @Override
    public Privilege addPrivilege(Privilege privilege) {
        return privilegeRepository.save(privilege);
    }

    @Override
    public Privilege updatePrivilege(Privilege privilege) {
        return privilegeRepository.save(privilege);
    }

    @Override
    public void deletePrivilege(int id) {
    privilegeRepository.deleteById(id);
    }

    @Override
    public Privilege getPrivilegeById(int id) {
        return privilegeRepository.findById(id).orElse(null);
    }

    @Override
    public List<Privilege> getAllPrivileges() {
        return privilegeRepository.findAll();
    }

    @Transactional
    @Override
    public Privilege addAndAssignPrivilegeToRole(Privilege privilege, int idRole) {
        Role role = roleRepository.findById(idRole).orElse(null);
        if (role == null) {
            throw new RuntimeException("Role not found");
        }
        if (privilege.getRoles() == null) {
            privilege.setRoles(new HashSet<>());
        }
        privilege.getRoles().add(role);
        return privilegeRepository.save(privilege);
    }

    @Override
    public Privilege AssignPrivilegeToRole(int idPrivilege, int idRole) {
        Privilege privilege = privilegeRepository.findById(idPrivilege).orElse(null);
        Role role = roleRepository.findById(idRole).orElse(null);

        privilege.getRoles().add(role);
        return privilegeRepository.save(privilege);
    }

}
