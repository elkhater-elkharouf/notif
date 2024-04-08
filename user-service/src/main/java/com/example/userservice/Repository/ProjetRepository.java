package com.example.userservice.Repository;

import com.example.userservice.Entities.Projet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjetRepository extends JpaRepository<Projet , Integer> {
}
