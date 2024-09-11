package com.example.kadai_002.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.kadai_002.service.StripeService;
import com.example.kadai_002.service.UserService;

@RestController
@RequestMapping("/stripe")
public class StripeWebhookController {
	private final StripeService stripeService;
	private final UserService userService;

	@Value("${stripe.api-key}")
	private String stripeApiKey;

	@Value("${stripe.webhook-secret}")
	private String webhookSecret;

	public StripeWebhookController(StripeService stripeService, UserService userService) {
		this.stripeService = stripeService;
		this.userService = userService;
	}
}

