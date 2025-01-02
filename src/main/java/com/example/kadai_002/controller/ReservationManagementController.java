package com.example.kadai_002.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.kadai_002.entity.Reservation;
import com.example.kadai_002.repository.ReservationRepository;
import com.example.kadai_002.security.UserDetailsImpl;

@Controller
@RequestMapping("/reservations")
public class ReservationManagementController {
	private final ReservationRepository reservationRepository;

	public ReservationManagementController(ReservationRepository reservationRepository) {
		this.reservationRepository = reservationRepository;
	}

	@GetMapping
	public String index(@AuthenticationPrincipal UserDetailsImpl userDetails,
			@PageableDefault(page = 0, size = 10, sort = "reservationDate", direction = Direction.DESC) Pageable pageable,
			Model model) {
		Page<Reservation> reservationPage = reservationRepository.findByUserId(userDetails.getId(), pageable);
		model.addAttribute("reservationPage", reservationPage);
		return "reservations/index";
	}
	
	@GetMapping("/{reservationId}/cancel")
	public String cancel(@PathVariable("reservationId") Integer reservationId,
	                    Model model) {
	    Reservation reservation = reservationRepository.findById(reservationId)
	            .orElseThrow(() -> new IllegalArgumentException("Invalid reservation ID"));
	    
	    model.addAttribute("reservation", reservation);
	    return "reservations/cancel";
	}

	@PostMapping("/{reservationId}/delete")
	public String delete(@PathVariable("reservationId") Integer reservationId,
	                    RedirectAttributes redirectAttributes) {
	    Reservation reservation = reservationRepository.findById(reservationId)
	            .orElseThrow(() -> new IllegalArgumentException("Invalid reservation ID"));
	    
	    reservationRepository.delete(reservation);
	    redirectAttributes.addFlashAttribute("successMessage", "予約をキャンセルしました。");
	    return "redirect:/reservations";
	}
}