package com.example.userservice.Services.User;
import okhttp3.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


@Service
public class SmsService {

    private final OkHttpClient client = new OkHttpClient();

    public void sendSms(File excelFile , String messageTemplate) throws IOException {
        Map<String, Map<String, String>> data = extractDataFromExcel(excelFile);

        for (Map.Entry<String, Map<String, String>> entry : data.entrySet()) {
            String message = createMessage(messageTemplate ,entry.getValue());

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\"messages\":[{\"destinations\":[{\"to\":\"" + entry.getKey() + "\"}],\"from\":\"ServiceSMS\",\"text\":\"" + message + "\"}]}");
            Request request = new Request.Builder()
                    .url("https://ggxede.api.infobip.com/sms/2/text/advanced")
                    .method("POST", body)
                    .addHeader("Authorization", "App 62f1553759740ee7ce24bf6c57b19f98-0e808fd4-504e-496e-b049-2ac19ecf74ac")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build();
            Response response = client.newCall(request).execute();
        }
    }

    private Map<String, Map<String, String>> extractDataFromExcel(File excelFile) throws IOException {
        Map<String, Map<String, String>> data = new HashMap<>();

        try (FileInputStream fis = new FileInputStream(excelFile);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            for (Row row : sheet) {
                if (row.getRowNum() != 0) {
                    Map<String, String> rowData = new LinkedHashMap<>();
                    String phoneNumber = getCellValueAsString(row.getCell(0));

                    for (Cell cell : row) {
                        if (cell.getColumnIndex() > 0) {
                            String header = getCellValueAsString(headerRow.getCell(cell.getColumnIndex()));
                            String value = getCellValueAsString(cell);
                            rowData.put(header, value);
                        }
                    }

                    data.put(phoneNumber, rowData);
                }
            }

            return data;
        }
    }

    private String getCellValueAsString(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    private String createMessage(String template, Map<String, String> placeholders) {
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            template = template.replace("${" + entry.getKey() + "}", entry.getValue());
        }
        return template;
    }
}
