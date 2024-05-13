package com.example.userservice.Services.User;

import com.example.userservice.Entities.GenericNotification;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
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
public class PPTService {

    @Autowired
    EmailServiceImpl emailService ;
    public ByteArrayInputStream generatePPTFilesFromExcel(File excelFile, File pptTemplate) throws IOException, InvalidFormatException, MessagingException {
        // Load Excel file
        Workbook workbook = new XSSFWorkbook(excelFile);
        Sheet sheet = workbook.getSheetAt(0); // Assuming only one sheet

        // Load ppt template
        XMLSlideShow ppt = new XMLSlideShow(new FileInputStream(pptTemplate));
        // Iterate over Excel rows

        // Cell emailCell = null;
        for (Row row : sheet) {
            if (row.getRowNum()!=0 ){
                Cell fnameCell = row.getCell(1); // Assuming Fname is in the second column
                Cell lnameCell = row.getCell(2);
                Cell emailCell = row.getCell(3) ;

                if (fnameCell != null && emailCell != null && lnameCell !=null ) {
                    String fname = fnameCell.getStringCellValue();
                    String lname = lnameCell.getStringCellValue();
                    String email=emailCell.getStringCellValue();


                    // Clone the template for each Fname
                    XMLSlideShow pptxFilePath = new XMLSlideShow(new FileInputStream(pptTemplate));


                    replacePlaceholder(pptxFilePath, fname , lname); // Replace placeholder with Fname
                    // Save the modified document
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    pptxFilePath.write(outputStream);

                    // Send email
                    sendEmail(email, fname, outputStream.toByteArray());

                    outputStream.close();
                }
            }
        }
        // Close resources
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        workbook.close();
        ppt.close();
        return in;

    }
    private void replacePlaceholder(XMLSlideShow pptxFilePath, String fname, String lname) {
        for (XSLFSlide slide : pptxFilePath.getSlides()) {
            for (XSLFShape shape : slide.getShapes()) {
                if (shape instanceof XSLFTextShape) {
                    XSLFTextShape textShape = (XSLFTextShape) shape;
                    for (XSLFTextParagraph paragraph : textShape) {
                        for (XSLFTextRun run : paragraph) {
                            String text = run.getRawText();
                            if (text != null) {
                                text = text.replace("nom", fname.toUpperCase());
                                text = text.replace("LastName", lname.toUpperCase());
                                run.setText(text);
                            }
                        }
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
        Map<String, Object> myHashMap = new HashMap<>();
        myHashMap.put("fname",fname);
// Définissez les attributs dans GenericNotification
        genericNotification.setAttributes(myHashMap);

        // Envoyez l'e-mail en utilisant le service d'e-mail
        try {
            emailService.sendEmailSpecificTemplate(genericNotification);

        } catch (MessagingException e) {
            // Gérer les erreurs de messagerie ici
            e.printStackTrace();
        }
//
    }
}
