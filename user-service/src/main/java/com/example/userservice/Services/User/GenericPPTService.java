package com.example.userservice.Services.User;
import com.example.userservice.Entities.GenericNotification;
import com.example.userservice.Repository.EmailTemplateRepository;
import lombok.AllArgsConstructor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xslf.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
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
public class GenericPPTService {
    EmailTemplateRepository emailTemplateRepository;
    @Autowired
    EmailServiceImpl emailService;


    public ByteArrayInputStream generatePPTFilesFromExcel(File excelFile, File pptTemplate) throws IOException, InvalidFormatException, MessagingException {
        Workbook workbook = new XSSFWorkbook(excelFile);
        Sheet sheet = workbook.getSheetAt(0); // Assuming only one sheet

        // Read header row
        Row headerRow = sheet.getRow(0);
        Map<String, Integer> headerMap = new HashMap<>();
        for (Cell cell : headerRow) {
            if (cell != null) {
                headerMap.put(cell.getStringCellValue(), cell.getColumnIndex());
                System.out.println("Header: " + cell.getStringCellValue() + " at index: " + cell.getColumnIndex()); // Debugging line
            }
        }
        // Create a ByteArrayOutputStream to store the modified PPTX files
        ByteArrayOutputStream allPptOutputStream = new ByteArrayOutputStream();
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
                    } else {
                        fieldValues.put(entry.getKey(), ""); // Handle missing cells
                    }
                }

                System.out.println("Field Values: " + fieldValues); // Debugging line

                String email = fieldValues.get("email"); // Assuming 'email' column exists
                if (email != null && !email.isEmpty()) {
                    XMLSlideShow clonedPpt = new XMLSlideShow(new FileInputStream(pptTemplate));
                    replacePlaceholders(clonedPpt, fieldValues);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    clonedPpt.write(outputStream);

                    sendEmail(email, fieldValues.get("Fname"), outputStream.toByteArray());

                    outputStream.close();
                }
            }
        }

        // Close resources
        workbook.close();
        return new ByteArrayInputStream(allPptOutputStream.toByteArray()); // Return an empty input stream as a placeholder
    }

    private void replacePlaceholders(XMLSlideShow ppt, Map<String, String> fieldValues) {
        for (XSLFSlide slide : ppt.getSlides()) {
            for (XSLFShape shape : slide.getShapes()) {
                if (shape instanceof XSLFTextShape) {
                    XSLFTextShape textShape = (XSLFTextShape) shape;
                    for (XSLFTextParagraph paragraph : textShape) {
                        StringBuilder fullText = new StringBuilder();
                        for (XSLFTextRun run : paragraph) {
                            fullText.append(run.getRawText());
                        }

                        String paragraphText = fullText.toString();
                        System.out.println("Original Paragraph Text: " + paragraphText); // Debugging line

                        for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
                            String placeholder = "${" + entry.getKey() + "}";
                            String regex = "\\$\\{\\s*" + entry.getKey() + "\\s*\\}";
                            paragraphText = paragraphText.replaceAll(regex, entry.getValue());
                        }

                        // Replace text in each individual XSLFTextRun
                        int runIndex = 0;
                        for (XSLFTextRun run : paragraph) {
                            int runTextLength = run.getRawText().length();
                            if (runIndex < paragraphText.length()) {
                                int endIndex = Math.min(runIndex + runTextLength, paragraphText.length());
                                run.setText(paragraphText.substring(runIndex, endIndex));
                                runIndex += runTextLength;
                            } else {
                                run.setText("");
                            }
                        }

                        System.out.println("Updated Paragraph Text: " + paragraphText); // Debugging line
                    }
                }
            }
        }
    }
    private void sendEmail(String toEmail, String fname, byte[] attachmentData) throws MessagingException {
        GenericNotification genericNotification = GenericNotification.builder()
                .label("aid mubarek")
                .emailTo(toEmail)
                .attachmentData(attachmentData)
                .build();
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("fname", fname);
        genericNotification.setAttributes(attributes);

        try {
            emailService.sendEmailSpecificTemplate(genericNotification);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}