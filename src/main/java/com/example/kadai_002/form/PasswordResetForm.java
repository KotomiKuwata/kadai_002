package com.example.kadai_002.form;

import lombok.Data;

@Data
public class PasswordResetForm {
	
	 private String token;
	    private String newPassword;
	    private String confirmPassword;
}
