package com.example.userservice.Services.User;
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
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.io.*;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class WordService {
    private JavaMailSender javaMailSender;
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
                    text = text.replace("MONEY" , montant.toString()) ;
                    r.setText(text, 0);
                }
            }
        }
    }
    private void sendEmail(String toEmail, String fname ,byte[] attachmentData) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(toEmail);
        helper.setSubject("your generated docx file");

        helper.setText("Cher(e) " + fname + ",\n\nvous trouverez ci-joint le document d'augmentation de salaire \n\nCordialement,");

        helper.addAttachment("output_" + fname + ".docx", new ByteArrayResource(attachmentData));

      javaMailSender.send(message);

    }
}
      /*  public ByteArrayInputStream createWordFile(MultipartFile excelFile, MultipartFile wordTemplate) throws IOException {
            // Load the Word template
            XWPFDocument doc = new XWPFDocument(wordTemplate.getInputStream());
            System.out.println("hani khlat");

            // Load the Excel file
            Workbook workbook = new XSSFWorkbook(excelFile.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);

            // Iterate over the rows in the Excel file
            Iterator<Row> rowIterator = sheet.iterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                String fname = row.getCell(1).getStringCellValue();  // Assuming 'Fname' is in the second column

                // Replace the 'nom' field in the Word file with 'fname'
                for (XWPFParagraph p : doc.getParagraphs()) {
                    for (XWPFRun r : p.getRuns()) {
                        String text = r.getText(0);
                        if (text != null && text.contains("nom")) {
                            text = text.replace("nom", fname);
                            r.setText(text, 0);
                        }
                    }
                }
                System.out.println("hani khlat");
                // Write the modified Word file to a stream
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                doc.write(out);
                ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
                out.close();

                return in;
            }

            return null;
        }*/

