package com.example.kadai_002.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.kadai_002.entity.User;
import com.example.kadai_002.repository.UserRepository;

@Service
public class PasswordResetService {

	@Autowired
	private EmailService emailService;

	@Autowired
	private TokenService tokenService;

	@Autowired
	private UserRepository userRepository;

	public void requestPasswordReset(String email) {
		User user = userRepository.findByEmail(email);
		if (user != null) {
			String token = tokenService.createToken(user);
			emailService.sendPasswordResetEmail(user.getEmail(), token);
		}
	}

	public boolean resetPassword(String token, String newPassword) {
		User user = tokenService.validateToken(token);
		if (user != null) {
			user.setPassword(hashPassword(newPassword));
			userRepository.save(user);
			return true;
		}
		return false;
	}

	private String hashPassword(String password) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		return passwordEncoder.encode(password);
	}
}
