package com.example.userservice.Services.EmailNotification;

import com.example.userservice.Entities.EmailTemplate;
import com.example.userservice.Entities.GenericNotification;
import com.example.userservice.Repository.EmailTemplateRepository;
import lombok.AllArgsConstructor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
@AllArgsConstructor
public class HTMLService {
    EmailTemplateRepository emailTemplateRepository ;
    @Autowired
    EmailServiceImpl emailService ;


    public void generateAndSendEmailsFromExcel(File excelFile, String templateLabel) throws IOException, InvalidFormatException, MessagingException {
        Workbook workbook = new XSSFWorkbook(excelFile);
        Sheet sheet = workbook.getSheetAt(0); // Assuming only one sheet

        // Read header row
        Row headerRow = sheet.getRow(0);
        Map<String, Integer> headerMap = new HashMap<>();
        for (Cell cell : headerRow) {
            headerMap.put(cell.getStringCellValue(), cell.getColumnIndex());
        }

        // Fetch the email template from the database
        EmailTemplate emailTemplate = emailTemplateRepository.findEmailTemplateByLabel(templateLabel);
        if (emailTemplate == null) {
            throw new RuntimeException("Template not found");
        }

        // Iterate over Excel rows
        for (Row row : sheet) {
            if (row.getRowNum() != 0) {
                Map<String, String> fieldValues = new HashMap<>();
                for (Map.Entry<String, Integer> entry : headerMap.entrySet()) {
                    Cell cell = row.getCell(entry.getValue());
                    if (cell != null) {
                        switch (cell.getCellType()) {
                            case STRING:
                                fieldValues.put(entry.getKey(), cell.getStringCellValue());
                                break;
                            case NUMERIC:
                                fieldValues.put(entry.getKey(), String.valueOf(cell.getNumericCellValue()));
                                break;
                            default:
                                fieldValues.put(entry.getKey(), "");
                        }
                    }
                }

                String email = fieldValues.get("email"); // Assuming 'email' column exists
                if (email != null && !email.isEmpty()) {
                    sendEmailWithTemplate(email, fieldValues, emailTemplate);
                }
            }
        }

        workbook.close();
    }

    private void sendEmailWithTemplate(String toEmail, Map<String, String> fieldValues, EmailTemplate emailTemplate) throws MessagingException {
        GenericNotification genericNotification = GenericNotification.builder()
                .label(emailTemplate.getLabel())
                .emailTo(toEmail)
                .build();
        Map<String, Object> attributes = new HashMap<>(fieldValues);
        genericNotification.setAttributes(attributes);

        emailService.sendEmailSpecificTemplate(genericNotification);
    }

}
