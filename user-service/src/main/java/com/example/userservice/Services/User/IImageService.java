package com.example.userservice.Services.User;

import com.example.userservice.Entities.Image;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface IImageService {
    public List<Image> list();
    public Optional<Image> getOne(int id);
    public  void save(MultipartFile imagen, int idUser) throws IOException;
    public void delete(int id);
    public boolean exists(int id);
    public Image getByUserId(int userId);
}
