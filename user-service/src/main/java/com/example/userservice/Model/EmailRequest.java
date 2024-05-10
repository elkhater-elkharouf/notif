package com.example.userservice.Model;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmailRequest {
 private String toEmail;
   private  String body;
    private String subject;
    private String from;
    private String attachment;
}
