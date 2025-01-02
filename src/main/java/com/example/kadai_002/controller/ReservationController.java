package com.example.kadai_002.controller;

import java.time.LocalDateTime;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.kadai_002.entity.Store;
import com.example.kadai_002.form.ReservationInputForm;
import com.example.kadai_002.form.ReservationRegisterForm;
import com.example.kadai_002.repository.ReservationRepository;
import com.example.kadai_002.repository.StoreRepository;
import com.example.kadai_002.repository.UserRepository;
import com.example.kadai_002.security.UserDetailsImpl;
import com.example.kadai_002.service.ReservationService;

@Controller
@RequestMapping("/stores/{storeId}/reservations")
public class ReservationController {

	private final ReservationService reservationService;
	private final ReservationRepository reservationRepository;
	private final StoreRepository storeRepository;
	private final UserRepository userRepository;

	public ReservationController(ReservationService reservationService, StoreRepository storeRepository,
			UserRepository userRepository, ReservationRepository reservationRepository) {
		this.reservationService = reservationService;
		this.storeRepository = storeRepository;
		this.userRepository = userRepository;
		this.reservationRepository = reservationRepository;
	}

	@GetMapping("/input")
	public String input(@PathVariable("storeId") Integer storeId,
			@ModelAttribute @Validated ReservationInputForm reservationInputForm,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes,
			Model model) {

		Store store = storeRepository.findById(storeId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid store ID"));

		if (reservationInputForm.getReservationDate() != null &&
				reservationInputForm.getReservationTime() != null) {

			LocalDateTime reservationDateTime = LocalDateTime.of(
					reservationInputForm.getReservationDate(),
					reservationInputForm.getReservationTime());

			if (!reservationService.isValidReservation(store, reservationDateTime)) {
				bindingResult.rejectValue("reservationTime", "error.reservationTime",
						"選択された日時は予約できません。");
			}
		}

		if (bindingResult.hasErrors()) {
			model.addAttribute("store", store);
			return "stores/show";
		}

		redirectAttributes.addFlashAttribute("reservationInputForm", reservationInputForm);
		return "redirect:/stores/" + storeId + "/reservations/confirm";
	}

	/*@GetMapping("/confirm")
	public String confirm(@PathVariable("storeId") Integer storeId,
			@ModelAttribute ReservationInputForm reservationInputForm,
			Model model) {
		Store store = storeRepository.findById(storeId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid store ID"));
		model.addAttribute("store", store);
		model.addAttribute("reservationInputForm", reservationInputForm);
		return "reservations/confirm";
	}*/

	@GetMapping("/confirm")
	public String confirm(@PathVariable("storeId") Integer storeId,
			@ModelAttribute ReservationInputForm reservationInputForm,
			@AuthenticationPrincipal UserDetailsImpl userDetails,
			Model model) {
		Store store = storeRepository.findById(storeId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid store ID"));

		ReservationRegisterForm reservationRegisterForm = new ReservationRegisterForm();
		reservationRegisterForm.setStoreId(storeId);
		reservationRegisterForm.setUserId(userDetails.getId()); // UserDetailsImplから直接IDを取得
		reservationRegisterForm.setReservationDate(reservationInputForm.getReservationDate());
		reservationRegisterForm.setReservationTime(reservationInputForm.getReservationTime());
		reservationRegisterForm.setNumberOfPeople(reservationInputForm.getNumberOfPeople());

		model.addAttribute("store", store);
		model.addAttribute("reservationRegisterForm", reservationRegisterForm);
		return "reservations/confirm";
	}

	@PostMapping("/create")
	public String create(@ModelAttribute ReservationRegisterForm reservationRegisterForm,
			RedirectAttributes redirectAttributes) {
		reservationService.create(reservationRegisterForm);
		redirectAttributes.addFlashAttribute("successMessage", "予約が完了しました。");
		return "redirect:/reservations"; // 予約一覧ページにリダイレクト
	}
}