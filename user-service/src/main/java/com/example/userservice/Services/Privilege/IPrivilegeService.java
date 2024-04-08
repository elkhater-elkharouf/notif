package com.example.userservice.Services.Privilege;



import com.example.userservice.Entities.Privilege;

import java.util.List;

public interface IPrivilegeService {
    public Privilege addPrivilege(Privilege privilege);
    public Privilege updatePrivilege(Privilege privilege);
    public void deletePrivilege(int id);
    public Privilege getPrivilegeById(int id);
    public List<Privilege> getAllPrivileges();

    public Privilege addAndAssignPrivilegeToRole(Privilege privilege , int idRole);


    public Privilege AssignPrivilegeToRole (int idPrivilege , int idRole);
}
