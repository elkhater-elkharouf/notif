package com.example.userservice.Services.User;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryService {

    Cloudinary cloudinary;

    private Map<String, String> valuesMap = new HashMap<>();

    public CloudinaryService() {
        valuesMap.put("cloud_name", "dajgfiyke");
        valuesMap.put("api_key", "857378282899297");
        valuesMap.put("api_secret", "eAEkKxLxuN5A1sEltVDOkZ03rwg");
        cloudinary = new Cloudinary(valuesMap);
    }

    public Map upload(MultipartFile multipartFile) throws IOException {
        File file = convert(multipartFile);
        Map<String, Object> result = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
        boolean isDeleted = file.delete();
        if (isDeleted) {
            // File was successfully deleted
            System.out.println("File deleted successfully");
        } else {
            // File deletion failed
            System.out.println("Failed to delete file");
            // You might want to handle this case accordingly
        }
        return result;
    }

    public Map delete(String id) throws IOException {
        Map result = cloudinary.uploader().destroy(id, ObjectUtils.emptyMap());
        return result;
    }

    private File convert(MultipartFile multipartFile) throws IOException {
        File file = new File(multipartFile.getOriginalFilename());
        try (FileOutputStream fo = new FileOutputStream(file)) {
            fo.write(multipartFile.getBytes());
        }
        return file;
    }

}