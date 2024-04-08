package com.example.userservice.Services.User;
import com.example.userservice.Entities.User;
import org.apache.poi.ss.usermodel.*;
import com.example.userservice.Repository.UserRepository;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExportUsersExcel {
    private final UserRepository userRepository ;
    public ExportUsersExcel(UserRepository userRepository ){this.userRepository=userRepository ;}
    public ByteArrayInputStream exportUsers(String department) throws IOException{
        String[] columns ={"idUser","Fname","Lname","email"};
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Users");

        // Header row
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }
        // Data Rows
        List<User> users = userRepository.findByDepartment(department) ;
        int rowNum = 1;
        for (User user : users) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(user.getIdUser());
            row.createCell(1).setCellValue(user.getFname());
            row.createCell(2).setCellValue(user.getLname());
            row.createCell(3).setCellValue(user.getEmail());
        }
        // Write to stream
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        out.close();

        return in;
    }

/*
    public void replaceNamesInWordFile(String inputFilePath, String outputFilePath, String nameToReplace, String replacement) throws IOException {
        FileInputStream fis = new FileInputStream(inputFilePath);
        XWPFDocument document = new XWPFDocument(fis);
        fis.close();

        // Parcourir tous les paragraphes et remplacer les noms
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            List<XWPFRun> runs = paragraph.getRuns();
            for (XWPFRun run : runs) {
                String text = run.getText(0);
                if (text != null && text.contains(nameToReplace)) {
                    text = text.replace(nameToReplace, replacement);
                    run.setText(text, 0);
                }
            }
        }

        // Parcourir toutes les tables et remplacer les noms
        for (XWPFTable table : document.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        for (XWPFRun run : paragraph.getRuns()) {
                            String text = run.getText(0);
                            if (text != null && text.contains(nameToReplace)) {
                                text = text.replace(nameToReplace, replacement);
                                run.setText(text, 0);
                            }
                        }
                    }
                }
            }
        }

        // Écrire dans le fichier de sortie
        FileOutputStream fos = new FileOutputStream(outputFilePath);
        document.write(fos);
        fos.close();
        document.close();
    }

    public void createTemplateForEachUser(String inputFilePath, String outputFolderPath, List<User> users) throws IOException {
        File outputFolder = new File(outputFolderPath);
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }

        for (User user : users) {
            String outputFile = outputFolderPath + "/" + user.getFname() + ".docx";
            replaceNamesInWordFile(inputFilePath, outputFile, "{Fname}", user.getFname());
        }
    }
*/


    public String createWordDocumentsFromExcel(String excelFilePath, String wordTemplatePath) {
        // Lire les noms des utilisateurs à partir du fichier Excel
        List<String> Fname = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(new File(excelFilePath));
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                Cell cell = row.getCell(1); // Assumer que le nom est dans la première colonne
                Fname.add(cell.getStringCellValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        String fileName = null;

        // Créer un document Word pour chaque nom d'utilisateur
        for (String fname : Fname) {
            // Charger le modèle Word
            XWPFDocument document;
            try (FileInputStream fis = new FileInputStream(new File(wordTemplatePath))) {
                document = new XWPFDocument(fis);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            // Remplacer le champ de nom dans le document
            for (XWPFParagraph p : document.getParagraphs()) {
                for (XWPFRun r : p.getRuns()) {
                    String text = r.getText(0);
                    if (text != null && text.contains("fname")) {
                        text = text.replace("fname", fname);
                        r.setText(text, 0);
                    }
                }
            }

            // Écrire le document Word
            fileName = fname + ".docx";
            try (FileOutputStream out = new FileOutputStream(new File(fileName))) {
                document.write(out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return fileName;
    }

}
