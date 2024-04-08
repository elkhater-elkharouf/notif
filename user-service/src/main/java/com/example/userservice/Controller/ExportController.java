package com.example.userservice.Controller;


import com.example.userservice.Entities.User;
import com.example.userservice.Services.User.ExportUsersExcel;
import com.example.userservice.Services.User.UserServiceImp;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController

public class ExportController {
   private final UserServiceImp userService ;
    private final ExportUsersExcel exportUsersExcel ;

    @Autowired
    public ExportController(ExportUsersExcel exportUsersExcel, UserServiceImp userService) {
        this.exportUsersExcel = exportUsersExcel;
        this.userService = userService;
    }
        @GetMapping(value = "/export/users/{department}", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<InputStreamResource> exportArticles(@PathVariable("department") String department) throws IOException {
        ByteArrayInputStream in = exportUsersExcel.exportUsers(department);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=users.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }
/*
    @GetMapping(value = "/export/templates")
    public ResponseEntity<String> createTemplatesForUsers(@RequestParam("inputFile") String inputFile,
                                                          @RequestParam("outputFolder") String outputFolder) {
        try {
            List<User> users = userService.getAllUsers(); // Récupérer la liste des utilisateurs depuis le service
            exportUsersExcel.createTemplateForEachUser(inputFile, outputFolder, users);
            return ResponseEntity.ok("Templates created successfully for each user.");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("An error occurred while creating templates for users.");
        }
    }*/

    @GetMapping("/createWordDocumentsFromExcel")
    public ResponseEntity<Resource> createWordDocumentsFromExcel(@RequestPart ("excelFilePath") String excelFilePath, @RequestPart ("wordTemplatePath") String wordTemplatePath) {
        String fileName = exportUsersExcel.createWordDocumentsFromExcel(excelFilePath, wordTemplatePath);
        Path path = Paths.get(fileName);
        Resource resource;
        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Issue in reading the file.", e);
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename())
                .body(resource);
    }
}
