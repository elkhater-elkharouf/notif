package com.example.userservice.Services.EmailNotification;

import com.example.userservice.Entities.GenericNotification;
import com.example.userservice.Repository.EmailTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.mail.MessagingException;

import lombok.AllArgsConstructor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
@Service
@Transactional
@AllArgsConstructor
public class GenericWordService {
    EmailTemplateRepository emailTemplateRepository;
    @Autowired
    EmailServiceImpl emailService ;

    public ByteArrayInputStream generateWordFilesFromExcelTest(File excelFile, File wordTemplate) throws IOException, InvalidFormatException, MessagingException {
        Workbook workbook = new XSSFWorkbook(excelFile);
        Sheet sheet = workbook.getSheetAt(0); // Assuming only one sheet

        // Load Word template
        XWPFDocument doc = new XWPFDocument(OPCPackage.open(wordTemplate));

        // Read header row
        Row headerRow = sheet.getRow(0);
        Map<String, Integer> headerMap = new HashMap<>();
        for (Cell cell : headerRow) {
            headerMap.put(cell.getStringCellValue(), cell.getColumnIndex());
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
                    XWPFDocument clonedDoc = new XWPFDocument(OPCPackage.open(wordTemplate));
                    replacePlaceholders(clonedDoc, fieldValues);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    clonedDoc.write(outputStream);

                    sendEmail(email, fieldValues.get("Fname"), outputStream.toByteArray());

                    outputStream.close();
                }
            }
        }

        // Close resources
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        workbook.close();
        doc.close();
        return in;
    }


    private void replacePlaceholders(XWPFDocument doc, Map<String, String> fieldValues) {
        for (XWPFParagraph p : doc.getParagraphs()) {
            String paragraphText = p.getText();
            for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
                String placeholder = "${" + entry.getKey() + "}";
                paragraphText = paragraphText.replace(placeholder, entry.getValue());
            }
            // Clear the runs and set the updated text in a single run
            p.getRuns().forEach(r -> r.setText("", 0));
            if (!paragraphText.isEmpty()) {
                p.createRun().setText(paragraphText, 0);
            }
        }
    }

    private void sendEmail(String toEmail, String fname, byte[] attachmentData) throws MessagingException {
        GenericNotification genericNotification = GenericNotification.builder()
                .label("augmentation de salaire")
                .emailTo(toEmail)
                .attachmentData(attachmentData)
                .build();
        Map<String, Object> myHashMap = new HashMap<>();
        myHashMap.put("fname", fname);
        myHashMap.put("attachmentLink", attachmentData);
        genericNotification.setAttributes(myHashMap);

        try {
            emailService.sendEmailSpecificTemplate(genericNotification);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}