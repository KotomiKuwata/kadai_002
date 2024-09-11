package com.example.kadai_002.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.kadai_002.entity.Favorite;
import com.example.kadai_002.entity.User;
import com.example.kadai_002.service.FavoriteService;
import com.example.kadai_002.service.UserService;

@Controller
@RequestMapping("/favorites")
public class FavoriteController {

	private final UserService userService;
	private final FavoriteService favoriteService;
	
	
	public FavoriteController(UserService userService, FavoriteService favoriteService) {
		this.userService = userService;
		this.favoriteService = favoriteService;
	
	}

	@GetMapping
	public String listFavorites(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		User user = userService.findByEmail(userDetails.getUsername());
		List<Favorite> favorites = favoriteService.getFavoritesByUser(user);
		model.addAttribute("favorites", favorites);
		return "favorites/index";
	}
}
