package com.example.userservice.Services.User;
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
public class WordController {
    private final ExportUsersExcel exportUsersExcel;
    private final WordService wordExportService;

    @Autowired
    public WordController(ExportUsersExcel exportUsersExcel, WordService wordExportService) {
        this.exportUsersExcel = exportUsersExcel;
        this.wordExportService = wordExportService;
    }

    @PostMapping("/export/word/from/excel")
    public ResponseEntity<InputStreamResource> exportWordFromExcel(
            @RequestPart("excelFile") MultipartFile excelFile,
            @RequestPart("wordTemplate") MultipartFile wordTemplate
            ) {
        try {
            File tempExcelFile = File.createTempFile("temp_excel", ".xlsx");
            excelFile.transferTo(tempExcelFile);

            File tempWordTemplate = File.createTempFile("temp_word_template", ".docx");
            wordTemplate.transferTo(tempWordTemplate);
            ByteArrayInputStream in =  wordExportService.generateWordFilesFromExcel(tempExcelFile, tempWordTemplate);
            System.out.println(in.toString());
            // Clean up temporary files if needed
            tempExcelFile.delete();
            tempWordTemplate.delete();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "word_files.docx");

          //  return new ResponseEntity<>(byteArrayOutputStream.toByteArray(), headers, HttpStatus.OK);
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.docx"))
                    .body(new InputStreamResource(in));
                    ///ResponseEntity.ok().body("Word files generated successfully.");
        } catch (IOException | InvalidFormatException |MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("mamcheeeeeeetech.", e);
        }
    }
}
/*
    @GetMapping("/download/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        Resource file = new UrlResource(Paths.get(filename).toUri());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }*/

