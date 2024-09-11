package com.example.kadai_002.service;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.kadai_002.entity.Token;
import com.example.kadai_002.entity.User;
import com.example.kadai_002.repository.TokenRepository;

@Service
public class TokenService {
	@Autowired
	private TokenRepository tokenRepository;

	public String createToken(User user) {
		String token = UUID.randomUUID().toString();
		Token passwordResetToken = new Token();
		passwordResetToken.setToken(token);
		passwordResetToken.setUser(user);

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR, 24); // トークンは24時間有効
		passwordResetToken.setExpiryDate(calendar.getTime());

		tokenRepository.save(passwordResetToken);

		return token;
	}

	public User validateToken(String token) {
		Token passwordResetToken = tokenRepository.findByToken(token);
		if (passwordResetToken == null || passwordResetToken.getExpiryDate().before(new Date())) {
			return null; // トークンが無効
		}
		return passwordResetToken.getUser();
	}
}
