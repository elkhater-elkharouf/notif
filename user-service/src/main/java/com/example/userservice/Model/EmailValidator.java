package com.example.userservice.Model;

import lombok.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Data
@NoArgsConstructor
@Getter
@Setter
public class EmailValidator {


        private static final String EMAIL_REGEX =
                "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        private static final Pattern pattern = Pattern.compile(EMAIL_REGEX);

        public static boolean isValid(String email) {
            Matcher matcher = pattern.matcher(email);
            return matcher.matches();
        }
}
