package com.example.userservice.Repository;

import com.example.userservice.Entities.Image;
import com.example.userservice.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Integer> {
    List<Image> findByOrderById();
    Image findByUser(User user);
}
