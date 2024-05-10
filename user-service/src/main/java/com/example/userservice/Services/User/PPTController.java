package com.example.userservice.Services.User;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
@RestController
public class PPTController {
    private final ExportUsersExcel exportUsersExcel;
    private final PPTService pptService;

    @Autowired
    public PPTController(ExportUsersExcel exportUsersExcel, PPTService pptService) {
        this.exportUsersExcel = exportUsersExcel;
        this.pptService = pptService;
    }

    @PostMapping("/export/ppt/from/excel")
    public ResponseEntity<InputStreamResource> exportWordFromExcel(
            @RequestPart("excelFile") MultipartFile excelFile,
            @RequestPart("pptTTemplate") MultipartFile pptTTemplate
    ) {
        try {
            File tempExcelFile = File.createTempFile("temp_excel", ".xlsx");
            excelFile.transferTo(tempExcelFile);

            File tempWordTemplate = File.createTempFile("temp_ppt_template", ".pptxx");
            pptTTemplate.transferTo(tempWordTemplate);
            ByteArrayInputStream in =  pptService.generatePPTFilesFromExcel(tempExcelFile, tempWordTemplate);
            System.out.println(in.toString());
            // Clean up temporary files if needed
            tempExcelFile.delete();
            tempWordTemplate.delete();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "ppt_files.pptx");

            //  return new ResponseEntity<>(byteArrayOutputStream.toByteArray(), headers, HttpStatus.OK);
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.pptx"))
                    .body(new InputStreamResource(in));
            ///ResponseEntity.ok().body("Word files generated successfully.");
        } catch (IOException | InvalidFormatException | MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("mamcheeeeeeetech.", e);
        }
    }
}