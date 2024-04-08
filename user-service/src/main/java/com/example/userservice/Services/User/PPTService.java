package com.example.userservice.Services.User;
import java.awt.Color; // Importer la classe Color pour spécifier la couleur du texte
import org.apache.poi.xslf.usermodel.XMLSlideShow; // Importer la classe XMLSlideShow pour manipuler les fichiers PowerPoint
import org.apache.poi.xslf.usermodel.XSLFShape; // Importer la classe XSLFShape pour manipuler les formes dans une diapositive
import org.apache.poi.xslf.usermodel.XSLFSlide; // Importer la classe XSLFSlide pour manipuler les diapositives
import org.apache.poi.xslf.usermodel.XSLFTextParagraph; // Importer la classe XSLFTextParagraph pour manipuler les paragraphes de texte
import org.apache.poi.xslf.usermodel.XSLFTextRun; // Importer la classe XSLFTextRun pour manipuler les runs de texte
import org.apache.poi.xslf.usermodel.XSLFTextShape; // I
import lombok.AllArgsConstructor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.core.io.ByteArrayResource;
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
public class PPTService {
    private JavaMailSender javaMailSender;
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
 /*   private void replacePlaceholder(XMLSlideShow pptxFilePath, String fname, String lname){


            for (XSLFSlide slide : pptxFilePath.getSlides()) {
                for (XSLFShape shape : slide.getShapes()) {
                    if (shape instanceof XSLFTextShape) {
                        XSLFTextShape textShape = (XSLFTextShape) shape;
                        String text = textShape.getText();
                        if (text != null) {
                            text = text.replace("nom", fname.toUpperCase());
                            text = text.replace("LastName", lname.toUpperCase());
                            textShape.setText(text);
                            // Définissez explicitement la police et la couleur du texte
                            for (XSLFTextParagraph paragraph : textShape) {
                                for (XSLFTextRun run : paragraph) {
                                    // Définir la police
                                    run.setFontFamily("Calibri (Corps)"); // Remplacez "Arial" par la police désirée

                                    // Définir la couleur du texte
                                    Color textColor = new Color(255, 255, 255); // Exemple de couleur rouge
                                    run.setFontColor(textColor);
                                    // Définir la taille de la police
                                    run.setFontSize(20.0);
                                }
                            }
                        }
                    }
                }
            }
            }*/

    private void sendEmail(String toEmail, String fname, byte[] attachmentData) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(toEmail);
        helper.setSubject("Your Generated ppt File");

        helper.setText("Dear " + fname + ",\n\nPlease find your generated ppt file attached.\n\nBest regards.");

        helper.addAttachment("output_" + fname + ".pptx", new ByteArrayResource(attachmentData));

        javaMailSender.send(message);
    }
}
