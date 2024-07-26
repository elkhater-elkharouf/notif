package com.example.userservice.Repository;

import com.example.userservice.Entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    List<Role> findByRoleName(String roleName);
}
