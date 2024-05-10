package com.example.userservice.Repository;

import com.example.userservice.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    public User findByEmail(String email);

    List<User> findAllByDepartmentIsNotNull();
    List<User> findAllByEnabled(boolean enabled);
    public List<User> findByDepartment(String department) ;

}