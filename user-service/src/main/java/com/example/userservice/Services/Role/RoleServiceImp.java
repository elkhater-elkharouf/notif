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

        for (Privilege privilege : role.getPrivileges()){
            System.out.println(privilege.getPrivilegeName()+"ssssssssssssssssss");
            Privilege p = privilegeRepository.findById(privilege.getIdPrivilege()).orElse(null);
            if (privilege.getRoles() == null) {
                privilege.setRoles(new HashSet<>());

            }
             roleRepository.save(role);
            Role r = roleRepository.findById(role.getIdRole()).orElse(null);
            r.getPrivileges().addAll(role.getPrivileges());
                privilege.getRoles().add(role);
                privilegeRepository.save(privilege);

        }
        return roleRepository.save(role);
        }


    @Override
    public Boolean AffectRoleToPrivilege(int idRole , int idPrivilege){
       boolean value;
        Role role = roleRepository.findById(idRole).orElse(null);
        Privilege privilege = privilegeRepository.findById(idPrivilege).orElse(null);
        if (role.getPrivileges() == null) {
            role.setPrivileges(new HashSet<>());

        }

       value = role.getPrivileges().add(privilege);
        return value;

    }


}
