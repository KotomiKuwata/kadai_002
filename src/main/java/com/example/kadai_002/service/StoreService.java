package com.example.kadai_002.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.kadai_002.entity.Store;
import com.example.kadai_002.form.StoreEditForm;
import com.example.kadai_002.form.StoreRegisterForm;
import com.example.kadai_002.repository.StoreRepository;

@Service

public class StoreService {
	private final StoreRepository storeRepository;

	public StoreService(StoreRepository storeRepository) {
		this.storeRepository = storeRepository;
	}

	@Transactional
	public void create(StoreRegisterForm storeRegisterForm) {
		Store store = new Store();
		MultipartFile imageFile = storeRegisterForm.getImageFile();

		if (!imageFile.isEmpty()) {
			String imageName = imageFile.getOriginalFilename();
			String hashedImageName = generateNewFileName(imageName);
			Path filePath = Paths.get("src/main/resources/static/storage/" + hashedImageName);
			try {
				Files.copy(imageFile.getInputStream(), filePath);
				store.setImageName(hashedImageName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		store.setName(storeRegisterForm.getName());
		store.setDescription(storeRegisterForm.getDescription());
		store.setOpeningHours(storeRegisterForm.getOpeningHours());
		store.setClosingTime(storeRegisterForm.getClosingTime());
		store.setPostalCode(storeRegisterForm.getPostalCode());
		store.setAddress(storeRegisterForm.getAddress());
		store.setPhoneNumber(storeRegisterForm.getPhoneNumber());
		store.setClosedDay(storeRegisterForm.getClosedDay());
		store.setCategoryId(storeRegisterForm.getCategoryId());

		storeRepository.save(store);
	}

	@Transactional
	public void update(StoreEditForm storeEditForm) {
		Store store = storeRepository.getReferenceById(storeEditForm.getId());
		MultipartFile imageFile = storeEditForm.getImageFile();

		if (imageFile != null && !imageFile.isEmpty()) {
			String imageName = imageFile.getOriginalFilename();
			String hashedImageName = generateNewFileName(imageName);
			Path filePath = Paths.get("src/main/resources/static/storage/" + hashedImageName);
			try {
				Files.copy(imageFile.getInputStream(), filePath);
				store.setImageName(hashedImageName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		store.setName(storeEditForm.getName());
		store.setDescription(storeEditForm.getDescription());
		store.setOpeningHours(storeEditForm.getOpeningHours());
		store.setClosingTime(storeEditForm.getClosingTime());
		store.setPostalCode(storeEditForm.getPostalCode());
		store.setAddress(storeEditForm.getAddress());
		store.setPhoneNumber(storeEditForm.getPhoneNumber());
		store.setClosedDay(storeEditForm.getClosedDay());
		store.setCategoryId(storeEditForm.getCategoryId());

		storeRepository.save(store);
	}

	// UUIDを使って生成したファイル名を返す
	public String generateNewFileName(String fileName) {
		String[] fileNames = fileName.split("\\.");
		for (int i = 0; i < fileNames.length - 1; i++) {
			fileNames[i] = UUID.randomUUID().toString();
		}
		String hashedFileName = String.join(".", fileNames);
		return hashedFileName;
	}

	// 画像ファイルを指定したファイルにコピーする
	public void copyImageFile(MultipartFile imageFile, Path filePath) {
		try {
			Files.copy(imageFile.getInputStream(), filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<String> getBusinessHours(Store store, LocalDate date) {
		if (store.getOpeningHours() == null || store.getClosingTime() == null) {
			return new ArrayList<>();
		}

		List<String> hours = new ArrayList<>();
		LocalTime openingTime = store.getOpeningHours();
		LocalTime closingTime = store.getClosingTime();
		// 閉店2時間前までを最終予約時間とする
		LocalTime lastReservationTime = closingTime.minusHours(2);

		LocalTime currentTime = openingTime;
		while (currentTime.isBefore(lastReservationTime) || currentTime.equals(lastReservationTime)) {
			hours.add(String.format("%02d:%02d", currentTime.getHour(), currentTime.getMinute()));
			currentTime = currentTime.plusMinutes(30);
		}

		return hours;
	}

	public boolean isClosedDay(Store store, LocalDate date) {
		String closedDay = store.getClosedDay();
		if (closedDay == null || closedDay.isEmpty()) {
			return false;
		}

		DayOfWeek dayOfWeek = date.getDayOfWeek();
		String[] closedDays = closedDay.split(",");

		for (String day : closedDays) {
			day = day.trim();
			boolean isClosed = switch (day) {
			case "月曜日" -> dayOfWeek == DayOfWeek.MONDAY;
			case "火曜日" -> dayOfWeek == DayOfWeek.TUESDAY;
			case "水曜日" -> dayOfWeek == DayOfWeek.WEDNESDAY;
			case "木曜日" -> dayOfWeek == DayOfWeek.THURSDAY;
			case "金曜日" -> dayOfWeek == DayOfWeek.FRIDAY;
			case "土曜日" -> dayOfWeek == DayOfWeek.SATURDAY;
			case "日曜日" -> dayOfWeek == DayOfWeek.SUNDAY;
			default -> false;
			};
			if (isClosed)
				return true;
		}
		return false;
	}

}
