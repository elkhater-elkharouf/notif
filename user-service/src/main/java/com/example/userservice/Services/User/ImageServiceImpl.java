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
    public void save(MultipartFile imageFile, int userId) throws IOException {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null)
            throw new IllegalArgumentException("No existe el usuario");

        Map<String, Object> uploadResult = cloudImage.upload(imageFile);
        BufferedImage bufferedImage = ImageIO.read(imageFile.getInputStream());
        String imageUrl = (String) uploadResult.get("url");
        String imageId = (String) uploadResult.get("public_id");

        Image image = user.getImage();
        if (image != null) {
            // Mettez à jour l'image existante

            image.setImagenUrl(imageUrl);
            image.setImagenId(imageId);
        } else {
            // Créez une nouvelle image
            image = new Image(imageFile.getOriginalFilename(), imageUrl, imageId);
            user.setImage(image);
            image.setUser(user);
        }
        imagenRepository.save(image);
    }

    @Override
    public void uploadImage(MultipartFile imageFile) throws IOException {
        // Téléchargez l'image sur le service de cloud
        Map<String, Object> uploadResult = cloudImage.upload(imageFile);
        BufferedImage bufferedImage = ImageIO.read(imageFile.getInputStream());

        // Récupérez l'URL de l'image et l'ID de l'image à partir des résultats du téléchargement
        String imageUrl = (String) uploadResult.get("url");
        String imageId = (String) uploadResult.get("public_id");

        // Créez une nouvelle instance d'image
        Image image = new Image(imageFile.getOriginalFilename(), imageUrl, imageId);

        // Sauvegardez l'image dans le repository
        imagenRepository.save(image);
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