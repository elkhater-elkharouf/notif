package com.example.userservice.Controller;
import com.example.userservice.Services.EmailNotification.GenericWordService;
import com.example.userservice.Services.EmailNotification.WordService;
import com.example.userservice.Services.User.ExportUsersExcel;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.mail.MessagingException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

@RestController
public class GenericWordController {
    private final ExportUsersExcel exportUsersExcel;
    private final WordService wordExportService;
    private  final com.example.userservice.Services.EmailNotification.GenericWordService GenericWordService;

    @Autowired
    public GenericWordController(ExportUsersExcel exportUsersExcel, WordService wordExportService, GenericWordService GenericWordService) {
        this.exportUsersExcel = exportUsersExcel;
        this.wordExportService = wordExportService;
        this.GenericWordService = GenericWordService;
    }

    @PostMapping("/export/wordGeneric/from/excel")
    public ResponseEntity<InputStreamResource> exportWordFromExcel(
            @RequestPart("excelFile") MultipartFile excelFile,
            @RequestPart("wordTemplate") MultipartFile wordTemplate
    ) {
        try {
            File tempExcelFile = File.createTempFile("temp_excel", ".xlsx");
            excelFile.transferTo(tempExcelFile);
            File tempWordTemplate = File.createTempFile("temp_word_template", ".docx");
            wordTemplate.transferTo(tempWordTemplate);
            ByteArrayInputStream in =  GenericWordService.generateWordFilesFromExcelTest(tempExcelFile, tempWordTemplate);
            System.out.println(in.toString());
            tempExcelFile.delete();
            tempWordTemplate.delete();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "word_files.docx");


            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.docx"))
                    .body(new InputStreamResource(in));

        } catch (IOException | InvalidFormatException |MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("mamcheeeeeeetech.", e);
        }
    }
}


