package com.example.kadai_002.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.kadai_002.entity.Review;
import com.example.kadai_002.entity.Store;
import com.example.kadai_002.entity.User;
import com.example.kadai_002.form.ReservationInputForm;
import com.example.kadai_002.form.ReviewInputForm;
import com.example.kadai_002.repository.ReviewRepository;
import com.example.kadai_002.repository.StoreRepository;
import com.example.kadai_002.security.UserDetailsImpl;
import com.example.kadai_002.service.ReviewService;
import com.example.kadai_002.service.UserService;

@Controller
@RequestMapping("/reviews")
public class ReviewController {
	private final ReviewService reviewService;
	private final StoreRepository storeRepository;
	private final ReviewRepository reviewRepository;
	private final UserService userService;

	public ReviewController(ReviewService reviewService, StoreRepository storeRepository,
			ReviewRepository reviewRepository, UserService userService) {
		this.reviewService = reviewService;
		this.storeRepository = storeRepository;
		this.reviewRepository = reviewRepository;
		this.userService = userService;
	}

	@GetMapping
	public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.DESC) Pageable pageable,
			Model model) {
		User user = userDetailsImpl.getUser();
		Page<Review> reviewPage = reviewRepository.findByUserOrderByTimestampDesc(user, pageable);
		model.addAttribute("reviewPage", reviewPage);
		return "reviews/index";
	}

	@PostMapping("/stores/{id}/reviews/input")
	public String input(@PathVariable(name = "id") Integer id,
			@ModelAttribute @Validated ReviewInputForm reviewInputForm,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes,
			Model model) {
		// バリデーションチェック
		if (bindingResult.hasErrors()) {
			Store store = storeRepository.getReferenceById(id);
			List<Review> reviews = reviewRepository.findByStoreId(id);
			model.addAttribute("store", store);
			model.addAttribute("reviews", reviews);
			model.addAttribute("reservationInputForm", new ReservationInputForm());

			// FieldErrorを使用してエラーメッセージを追加
			bindingResult.addError(new FieldError("reviewInputForm", "general", "レビュー内容に不備があります。"));

			return "stores/show";
		}

		redirectAttributes.addFlashAttribute("reviewInputForm", reviewInputForm);

		return "redirect:/stores/{id}/reviews/confirm";
	}

	@PostMapping("/stores/{id}/reviews/confirm")
	public String confirm(@PathVariable(name = "id") Integer id,
			@ModelAttribute @Validated ReviewInputForm reviewInputForm,
			BindingResult result,
			Model model) {
		if (result.hasErrors()) {
			return "stores/show";
		}

		model.addAttribute("store", storeRepository.getReferenceById(id));

		return "reviews/confirm";
	}

	@PostMapping("/stores/{id}/reviews/create")
	public String create(@PathVariable(name = "id") Integer id, @ModelAttribute ReviewInputForm reviewInputForm,
			@AuthenticationPrincipal UserDetails userDetails) {
		try {
			reviewInputForm.setStoreId(id);
			User user = userService.findByEmail(userDetails.getUsername());
			reviewInputForm.setUserId(user.getId());
			reviewService.create(reviewInputForm);
			return "redirect:/reviews?reviewed";
		} catch (Exception e) {
			System.err.println("Error occurred while creating review: " + e.getMessage());
			return "redirect:/stores/" + id + "?error";
		}
	}

	@GetMapping("/{id}/edit")
	public String edit(@PathVariable(name = "id") Integer id, Model model) {
		Optional<Review> optionalReview = reviewRepository.findById(id);
		if (optionalReview.isPresent()) {
			Review review = optionalReview.get();
			ReviewInputForm reviewInputForm = new ReviewInputForm();
			reviewInputForm.setRating(review.getRating());
			reviewInputForm.setComment(review.getComment());
			reviewInputForm.setStoreId(review.getStore().getId());
			reviewInputForm.setUserId(review.getUser().getId());
			model.addAttribute("review", review);
			model.addAttribute("reviewInputForm", reviewInputForm);
			return "reviews/edit";
		} else {
			// レビューが見つからない場合の処理
			return "redirect:/reviews"; // または適切なエラーページにリダイレクト
		}
	}

	@PostMapping("/{id}/update")
	public String update(@PathVariable(name = "id") Integer id,
			@ModelAttribute @Validated ReviewInputForm reviewInputForm,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			return "reviews/edit";
		}

		try {
			reviewService.update(id, reviewInputForm);
			redirectAttributes.addFlashAttribute("successMessage", "レビューが更新されました。");
			return "redirect:/reviews?edited";
		} catch (Exception e) {
			System.err.println("Error occurred while updating review: " + e.getMessage());
			return "redirect:/reviews/" + id + "/edit?error";
		}
	}

	@PostMapping("/{id}/delete")
	public String delete(@PathVariable(name = "id") Integer id,
			RedirectAttributes redirectAttributes) {
		reviewService.delete(id);
		redirectAttributes.addFlashAttribute("successMessage", "レビューが削除されました。");
		return "redirect:/reviews?deleted";
	}
}

/*@GetMapping("/stores/{id}/reviews/input")
public String input(@PathVariable(name = "id") Integer id,
		@ModelAttribute @Validated ReviewRegisterForm reviewRegisterForm,
		BindingResult bindingResult,
		RedirectAttributes redirectAttributes,
		Model model) {
	// 実際の処理
	Store store = storeRepository.getReferenceById(id);

	if (bindingResult.hasErrors()) {
		model.addAttribute("store", store);
		model.addAttribute("errorMessage", "投稿内容に不備があります。");
		return "stores/show";
	}

	redirectAttributes.addFlashAttribute("reviewRegisterForm", reviewRegisterForm);
	return "redirect:/stores/" + id + "/reviews/confirm";
}

@GetMapping("/confirm")
public String showCreateForm(Model model) {
	model.addAttribute("reviewRegisterForm", new ReviewRegisterForm());
	return "reviews/confirm";
}
 
 

@PostMapping("/confirm")
public String confirm(@ModelAttribute @Validated ReviewRegisterForm reviewRegisterForm,
		BindingResult bindingResult,
		RedirectAttributes redirectAttributes,
		@AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
	if (bindingResult.hasErrors()) {
		return "reviews/confirm";
	}

	Store store = storeRepository.getReferenceById(reviewRegisterForm.getStoreId());
	User user = userDetailsImpl.getUser();

	reviewService.create(reviewRegisterForm);

	redirectAttributes.addFlashAttribute("successMessage", "レビューを投稿しました。");
	return "redirect:/stores/" + store.getId();
}

@GetMapping("/stores/{id}")
public String showReviewForm(@PathVariable(name = "id") Integer id, Model model) {
    Store store = storeRepository.getReferenceById(id);
    List<Review> reviews = reviewService.getReviewsByStoreId(id);
    
    ReviewRegisterForm reviewRegisterForm = new ReviewRegisterForm();
    reviewRegisterForm.setStoreId(store.getId());
    model.addAttribute("reviewRegisterForm", reviewRegisterForm);

    model.addAttribute("store", store);
    model.addAttribute("reviews", reviews);

    return "stores/show";*/