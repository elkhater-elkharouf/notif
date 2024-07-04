package com.example.userservice.Controller;

import com.example.userservice.Services.EmailNotification.HTMLService;
import com.example.userservice.Services.EmailNotification.WordService;
import com.example.userservice.Services.User.ExportUsersExcel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.mail.MessagingException;

import java.io.File;
import java.io.IOException;

@RestController
public class HTMLController {


    private final ExportUsersExcel exportUsersExcel;
    private final WordService wordExportService;
    private final HTMLService htmlService;

    @Autowired
    public HTMLController(ExportUsersExcel exportUsersExcel, WordService wordExportService, HTMLService htmlService) {
        this.exportUsersExcel = exportUsersExcel;
        this.wordExportService = wordExportService;
        this.htmlService = htmlService;
    }

    @PostMapping("/export/email/from/excel")
    public ResponseEntity<String> sendEmailsFromExcel(
            @RequestPart("excelFile") MultipartFile excelFile,
            @RequestParam("templateLabel") String templateLabel
    ) {
        try {
            if (excelFile.isEmpty()) {
                throw new RuntimeException("Excel file is empty");
            }
            if (templateLabel == null || templateLabel.isEmpty()) {
                throw new RuntimeException("Template label is missing");
            }

            File tempExcelFile = File.createTempFile("temp_excel", ".xlsx");
            excelFile.transferTo(tempExcelFile);

            htmlService.generateAndSendEmailsFromExcel(tempExcelFile, templateLabel);

            tempExcelFile.delete();
            return ResponseEntity.ok().build();
        } catch (IOException | InvalidFormatException | MessagingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
