package com.example.kadai_002.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.kadai_002.entity.Category;
import com.example.kadai_002.entity.Store;
import com.example.kadai_002.entity.User;
import com.example.kadai_002.form.ReservationInputForm;
import com.example.kadai_002.form.ReviewInputForm;
import com.example.kadai_002.repository.CategoryRepository;
import com.example.kadai_002.repository.ReviewRepository;
import com.example.kadai_002.repository.StoreRepository;
import com.example.kadai_002.service.FavoriteService;
import com.example.kadai_002.service.UserService;

@Controller
@RequestMapping("/stores")
public class StoreController {
	private final StoreRepository storeRepository;
	private final CategoryRepository categoryRepository;
	private final ReviewRepository reviewRepository;
	private final UserService userService;
	private final FavoriteService favoriteService;

	public StoreController(StoreRepository storeRepository, CategoryRepository categoryRepository,
			ReviewRepository reviewRepository, UserService userService, FavoriteService favoriteService) {
		this.storeRepository = storeRepository;
		this.categoryRepository = categoryRepository;
		this.reviewRepository = reviewRepository;
		this.userService = userService;
		this.favoriteService = favoriteService;
	}

	@GetMapping
	public String index(@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "categoryName", required = false) String categoryName,
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
			Model model) {
		Page<Store> storePage;

		if (categoryName != null && !categoryName.isEmpty()) {
			storePage = storeRepository.findByCategory_Name(categoryName, pageable);
		} else if (keyword != null && !keyword.isEmpty()) {
			storePage = storeRepository.findByNameLike("%" + keyword + "%", pageable);
		} else {
			storePage = storeRepository.findAll(pageable);
		}

		model.addAttribute("storePage", storePage);
		model.addAttribute("keyword", keyword);
		model.addAttribute("categoryName", categoryName);
		List<Category> categories = categoryRepository.findAll();
		model.addAttribute("categories", categories);

		return "stores/index";
	}

	@PostMapping("/{id}/favorite")
	public String addFavorite(@PathVariable(name = "id") Integer id, @AuthenticationPrincipal UserDetails userDetails) {
		Store store = storeRepository.getReferenceById(id);
		User user = userService.findByEmail(userDetails.getUsername());
		favoriteService.addFavorite(user, store);
		return "redirect:/stores/" + id;
	}

	@PostMapping("/{id}/unfavorite")
	public String removeFavorite(@PathVariable(name = "id") Integer id,
			@AuthenticationPrincipal UserDetails userDetails) {
		Store store = storeRepository.getReferenceById(id);
		User user = userService.findByEmail(userDetails.getUsername());
		favoriteService.removeFavorite(user, store);
		return "redirect:/stores/" + id;
	}

	@GetMapping("/{id}")
	public String show(@PathVariable(name = "id") Integer id, Model model,
			@AuthenticationPrincipal UserDetails userDetails) {
		Store store = storeRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid store ID"));

		model.addAttribute("store", store);
		model.addAttribute("reviews", reviewRepository.findByStoreId(id));
		model.addAttribute("reservationInputForm", new ReservationInputForm());
		model.addAttribute("reviewInputForm", new ReviewInputForm());

		if (userDetails != null) {
			User user = userService.findByEmail(userDetails.getUsername());
			boolean isFavorite = favoriteService.isFavorite(user, store);
			model.addAttribute("isFavorite", isFavorite);
		} else {
			model.addAttribute("isFavorite", false);
		}

		return "stores/show";
	}
}