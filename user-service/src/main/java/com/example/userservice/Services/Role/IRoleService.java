package com.example.userservice.Services.Role;

import com.example.userservice.Entities.Role;

import java.util.List;

public interface IRoleService {
    public Role addRole(Role role);
    public Role updateRole(Role role);
    public void deleteRole(int id);
    public Role getRoleById(int id);
    public List<Role> getAllRoles();


    public Role AddRoleWithPrivilege(Role role);


    public Boolean AffectRoleToPrivilege(int idRole , int idPrivilege);

}
