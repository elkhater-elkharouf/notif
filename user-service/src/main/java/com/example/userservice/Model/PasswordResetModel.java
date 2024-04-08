package com.example.userservice.Model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PasswordResetModel {
	private String NewPassword;
	private String ConfirmPassword;
}
