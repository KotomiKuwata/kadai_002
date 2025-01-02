package com.example.kadai_002.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.kadai_002.entity.Store;
import com.example.kadai_002.repository.StoreRepository;
import com.example.kadai_002.service.StoreService;

@Controller
@RequestMapping("/api/stores")
public class StoreApiController {
	private final StoreService storeService;
	private final StoreRepository storeRepository;

	public StoreApiController(StoreService storeService, StoreRepository storeRepository) {
		this.storeService = storeService;
		this.storeRepository = storeRepository;
	}

	@GetMapping("/{storeId}/available-hours")
	@ResponseBody
	public Map<String, Object> getAvailableHours(@PathVariable Integer storeId, @RequestParam String date) {
		Store store = storeRepository.findById(storeId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid store ID"));

		LocalDate reservationDate = LocalDate.parse(date);
		Map<String, Object> response = new HashMap<>();

		// 定休日チェック
		boolean isClosed = storeService.isClosedDay(store, reservationDate);
		response.put("isClosed", isClosed);

		// 予約可能時間の取得
		if (!isClosed) {
			List<String> availableHours = storeService.getBusinessHours(store, reservationDate);
			response.put("availableHours", availableHours);
		} else {
			response.put("availableHours", new ArrayList<>());
		}

		return response;
	}
}