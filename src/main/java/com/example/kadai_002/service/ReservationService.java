package com.example.kadai_002.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.stereotype.Service;

import com.example.kadai_002.entity.Reservation;
import com.example.kadai_002.entity.Store;
import com.example.kadai_002.entity.User;
import com.example.kadai_002.form.ReservationRegisterForm;
import com.example.kadai_002.repository.ReservationRepository;
import com.example.kadai_002.repository.StoreRepository;
import com.example.kadai_002.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class ReservationService {

	private final ReservationRepository reservationRepository;
	private final StoreRepository storeRepository;
	private final UserRepository userRepository;
	private final StoreService storeService;

	public ReservationService(ReservationRepository reservationRepository, StoreRepository storeRepository,
			UserRepository userRepository, StoreService storeService) {
		this.reservationRepository = reservationRepository;
		this.storeRepository = storeRepository;
		this.userRepository = userRepository;
		this.storeService = storeService;
	}

	@Transactional
	public void create(ReservationRegisterForm reservationRegisterForm) {
		try {
			Reservation reservation = new Reservation();
			Store store = storeRepository.getReferenceById(reservationRegisterForm.getStoreId());
			User user = userRepository.getReferenceById(reservationRegisterForm.getUserId());
			LocalTime reservationTime = reservationRegisterForm.getReservationTime();
			LocalDate reservationDate = reservationRegisterForm.getReservationDate();

			reservation.setStore(store);
			reservation.setUser(user);
			reservation.setReservationTime(reservationTime);
			reservation.setReservationDate(reservationDate);
			reservation.setNumberOfPeople(reservationRegisterForm.getNumberOfPeople());

			reservationRepository.save(reservation);
		} catch (Exception e) {
			throw e; // トランザクションロールバック
		}
	}

	public boolean isValidReservationTime(Store store, LocalDateTime reservationDateTime) {

		LocalTime openingHours = store.getOpeningHours();
		LocalTime closingTime = store.getClosingTime();
		LocalTime reservationTime = reservationDateTime.toLocalTime();
		LocalTime lastReservationTime = closingTime;

		boolean isValidStartTime = openingHours.equals(reservationTime) || openingHours.isBefore(reservationTime);

		boolean isBeforeOrAtLastReservation = lastReservationTime.isAfter(reservationTime)
				|| reservationTime.equals(lastReservationTime);

		return isValidStartTime && isBeforeOrAtLastReservation;
	}

	public boolean isValidReservation(Store store, LocalDateTime reservationDateTime) {
		// 定休日チェック
		if (storeService.isClosedDay(store, reservationDateTime.toLocalDate())) {
			return false;
		}

		LocalTime reservationTime = reservationDateTime.toLocalTime();
		LocalTime openingHours = store.getOpeningHours();
		LocalTime closingTime = store.getClosingTime();
		LocalTime lastReservationTime = closingTime.minusHours(2);

		// 営業時間内かつ最終予約時間前かチェック
		return !reservationTime.isBefore(openingHours) &&
				!reservationTime.isAfter(lastReservationTime);
	}

	private boolean isClosedDay(Store store, LocalDate date) {
		String closedDay = store.getClosedDay();
		if (closedDay == null || closedDay.isEmpty()) {
			return false;
		}

		DayOfWeek dayOfWeek = date.getDayOfWeek();
		String[] closedDays = store.getClosedDay().split(",");

		for (String day : closedDays) {
			if (matchesJapaneseDayOfWeek(day.trim(), dayOfWeek)) {
				return true;
			}
		}
		return false;
	}

	private boolean matchesJapaneseDayOfWeek(String japaneseDayName, DayOfWeek dayOfWeek) {
		return switch (japaneseDayName) {
		case "月曜日" -> dayOfWeek == DayOfWeek.MONDAY;
		case "火曜日" -> dayOfWeek == DayOfWeek.TUESDAY;
		case "水曜日" -> dayOfWeek == DayOfWeek.WEDNESDAY;
		case "木曜日" -> dayOfWeek == DayOfWeek.THURSDAY;
		case "金曜日" -> dayOfWeek == DayOfWeek.FRIDAY;
		case "土曜日" -> dayOfWeek == DayOfWeek.SATURDAY;
		case "日曜日" -> dayOfWeek == DayOfWeek.SUNDAY;
		default -> false;
		};
	}
}
