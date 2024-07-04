package com.example.userservice.Services.EmailNotification;

import com.example.userservice.Entities.GenericNotification;
import com.example.userservice.Repository.EmailTemplateRepository;
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
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@AllArgsConstructor
public class WordService {

    EmailTemplateRepository emailTemplateRepository;
    @Autowired
    EmailServiceImpl emailService ;
    public ByteArrayInputStream generateWordFilesFromExcel(File excelFile, File wordTemplate) throws IOException, InvalidFormatException, MessagingException {
        // Load Excel file
        Workbook workbook = new XSSFWorkbook(excelFile);
        Sheet sheet = workbook.getSheetAt(0); // Assuming only one sheet

        // Load Word template
        XWPFDocument doc = new XWPFDocument(OPCPackage.open(wordTemplate));

        // Iterate over Excel rows

       // Cell emailCell = null;
        for (Row row : sheet) {
            if (row.getRowNum()!=0 ){
            Cell fnameCell = row.getCell(1); // Assuming Fname is in the second column
            Cell lnameCell = row.getCell(2);
            Cell emailCell = row.getCell(3) ;
            Cell montantCell =row.getCell(4) ;
            if (fnameCell != null && emailCell != null && lnameCell !=null  && montantCell !=null) {
                String fname = fnameCell.getStringCellValue();
                String lname = lnameCell.getStringCellValue();
                String email=emailCell.getStringCellValue();
                Double montant= montantCell.getNumericCellValue();
                // Clone the template for each Fname
                XWPFDocument clonedDoc = new XWPFDocument(OPCPackage.open(wordTemplate));
                replacePlaceholder(clonedDoc, fname ,lname, montant); // Replace placeholder with Fname
                // Save the modified document
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                clonedDoc.write(outputStream);

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
        doc.close();
        return in;

    }


    private void replacePlaceholder(XWPFDocument doc, String fname , String lname , Double montant ) {
        for (XWPFParagraph p : doc.getParagraphs()) {
            List<XWPFRun> runs = p.getRuns();
            for (XWPFRun r : runs) {
                String text = r.getText(0);


                if (text != null ) {
                    // Remplace le placeholder $nom$ par la valeur de Fname
                    text = text.replace("nom", fname.toUpperCase());
                    text = text.replace("LastName", lname.toUpperCase());
                    text = text.replace("montant" , montant.toString()) ;
                    r.setText(text, 0);
                }
            }
        }
    }
    private void sendEmail(String toEmail, String fname ,byte[] attachmentData) throws MessagingException {


        GenericNotification genericNotification = GenericNotification.builder()
                .label("augmentation de salaire")
                .emailTo(toEmail)
                .attachmentData(attachmentData)
                .build();
        Map<String, Object> myHashMap = new HashMap<>();
        myHashMap.put("fname",fname);
        myHashMap.put("attachmentLink", attachmentData);
        // Définissez les attributs dans GenericNotification
        genericNotification.setAttributes(myHashMap);

        // Envoyez l'e-mail en utilisant le service d'e-mail
        try {
            emailService.sendEmailSpecificTemplate(genericNotification);

        } catch (MessagingException e) {
            // Gérer les erreurs de messagerie ici
            e.printStackTrace();
        }


    }

}
