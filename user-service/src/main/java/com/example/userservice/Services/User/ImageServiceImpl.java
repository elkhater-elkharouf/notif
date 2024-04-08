package com.example.userservice.Services.User;

import com.example.userservice.Entities.Image;
import com.example.userservice.Entities.User;
import com.example.userservice.Repository.ImageRepository;
import com.example.userservice.Repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ImageServiceImpl implements IImageService {
    CloudinaryService cloudImage;
    ImageRepository imagenRepository;
    UserRepository userRepository;
    @Override
    public List<Image> list() {
        return imagenRepository.findByOrderById();
    }

    @Override
    public Optional<Image> getOne(int id) {
        return imagenRepository.findById(id);
    }

    @Override
    public  void save(MultipartFile imagen, int idUser) throws IOException {

        User user = userRepository.findById(idUser).orElse(null);
        if (user == null)
               new ResponseEntity<>("No existe el usuario", HttpStatus.NOT_FOUND);

        if (user.getImage() != null) {
            // User already has an image, update it
            Image existingImage = user.getImage();
            Map result = cloudImage.upload(imagen);
            BufferedImage bi = ImageIO.read(imagen.getInputStream());
            existingImage.setImagenUrl((String) result.get("url"));
            existingImage.setImagenId((String) result.get("public_id"));
              new ResponseEntity<>(imagenRepository.save(existingImage), HttpStatus.OK);
        }
        // User doesn't have an image, create a new one
        Map result = cloudImage.upload(imagen);
        BufferedImage bi = ImageIO.read(imagen.getInputStream());
        Image media = new Image((String) result.get("original_filename"),
                (String) result.get("url"),
                (String) result.get("public_id"));
        user.setImage(media);
        media.setUser(user);
          new ResponseEntity<>(imagenRepository.save(media), HttpStatus.CREATED);
    }


    @Override
    public void delete(int id) {
        imagenRepository.deleteById(id);
    }

    @Override
    public boolean exists(int id) {
        return imagenRepository.existsById(id);
    }

    @Override
    public Image getByUserId(int userId) {

        User user = userRepository.findById(userId).get();
        return imagenRepository.findByUser(user);

    }

}