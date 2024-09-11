package com.example.kadai_002.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private HttpServletRequest request;

	public void sendPasswordResetEmail(String email, String token) {
		String resetLink = generateResetLink(token);

		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("noreply@yourdomain.com");
		message.setTo(email);
		message.setSubject("パスワードリセット");
		message.setText("パスワードをリセットするには、以下のリンクをクリックしてください：\n" + resetLink);

		mailSender.send(message);
	}

	private String generateResetLink(String token) {
		String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
		return baseUrl + "/auth/reset-password?token=" + token;
	}

}
