package com.example.kadai_002.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.kadai_002.form.PasswordResetForm;
import com.example.kadai_002.service.PasswordResetService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/auth")
public class PasswordResetController {

	@Autowired
	private PasswordResetService passwordResetService;

	@GetMapping("/request-password-reset")
	public String showRequestPasswordResetForm() {
		return "auth/request-password-reset";
	}

	@PostMapping("/request-password-reset")
	public ResponseEntity<?> requestPasswordReset(@RequestParam String email) {
		passwordResetService.requestPasswordReset(email);
		return ResponseEntity.ok().body("パスワードリセット要求を受け付けました。メールをご確認ください。");
	}

	@GetMapping("/reset-password")
	public String showResetPasswordForm(@RequestParam String token, Model model) {
		PasswordResetForm form = new PasswordResetForm();
		form.setToken(token);
		model.addAttribute("passwordResetForm", form);
		return "auth/reset-password";
	}

	@PostMapping("/reset-password")
	public String resetPassword(@Valid @ModelAttribute PasswordResetForm form, BindingResult result, RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			return "auth/reset-password";
		}
		if (!form.getNewPassword().equals(form.getConfirmPassword())) {
			result.rejectValue("confirmPassword", "error.passwordMismatch", "パスワードが一致しません。");
			return "auth/reset-password";
		}
		boolean resetSuccess = passwordResetService.resetPassword(form.getToken(), form.getNewPassword());
		if (resetSuccess) {
		    redirectAttributes.addFlashAttribute("successMessage", "パスワードが変更できました。");
		    return "redirect:/user";
		} else {
			result.rejectValue("token", "error.invalidToken", "無効または期限切れのトークンです。");
			return "auth/reset-password";
		}
	}
}
