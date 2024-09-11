package com.example.kadai_002.service;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.kadai_002.entity.Reservation;
import com.example.kadai_002.entity.Store;
import com.example.kadai_002.entity.User;
import com.example.kadai_002.form.ReservationRegisterForm;
import com.example.kadai_002.repository.ReservationRepository;
import com.example.kadai_002.repository.StoreRepository;
import com.example.kadai_002.repository.UserRepository;

@Service
public class ReservationService {

	private final ReservationRepository reservationRepository;
	private final StoreRepository storeRepository;
	private final UserRepository userRepository;

	public ReservationService(ReservationRepository reservationRepository, StoreRepository storeRepository,
			UserRepository userRepository) {
		this.reservationRepository = reservationRepository;
		this.storeRepository = storeRepository;
		this.userRepository = userRepository;
	}

	@Transactional
	public void create(ReservationRegisterForm reservationRegisterForm) {
		try {
			Reservation reservation = new Reservation();
			Store store = storeRepository.getReferenceById(reservationRegisterForm.getStoreId());
			User user = userRepository.getReferenceById(reservationRegisterForm.getUserId());
			LocalDateTime reservationDatetime = reservationRegisterForm.getReservationDatetime();

			reservation.setStore(store);
			reservation.setUser(user);
			reservation.setReservationDatetime(reservationDatetime);
			reservation.setNumberOfPeople(reservationRegisterForm.getNumberOfPeople());

			reservationRepository.save(reservation);
			System.out.println("Reservation saved successfully: " + reservation);
		} catch (Exception e) {
			System.err.println("Error occurred while saving reservation: " + e.getMessage());
			throw e; // 再スローしてトランザクションをロールバック
		}
	}

	public boolean isValidReservationTime(Store store, LocalDateTime reservationDateTime) {

		LocalTime openingHours = store.getOpeningHours(); //開店時間の取得(例 10.17.17.17)
		LocalTime closingTime = store.getClosingTime(); //閉店時間の取得 (例 19.0.2.5)
		LocalTime reservationTime = reservationDateTime.toLocalTime(); //予約時間の取得
		LocalTime lastReservationTime = closingTime.minusHours(2); //最終予約受付時間（閉店２時間前 例 17.22.0.3）

		//予約開始時間
		boolean isValidStartTime = openingHours.equals(reservationTime) || openingHours.isBefore(reservationTime);

		// 日付をまたがない予約受付時間
		boolean isBeforeOrAtLastReservation = lastReservationTime.isAfter(reservationTime)
				|| reservationTime.equals(lastReservationTime);

		// 日付を跨ぐ予約受付時間
		boolean isAfterMidnight = openingHours.isAfter(reservationTime) && openingHours.isAfter(lastReservationTime);

		//日付を跨ぐ閉店時間
		boolean closingTimeisAfterMidnight = closingTime.isBefore(openingHours)
				&& !closingTime.equals(LocalTime.MIDNIGHT);

		return (//営業時間が日付をまたがない場合 openingHours < reservationTime < lastReservationTime
		isValidStartTime && isBeforeOrAtLastReservation)
				||
				(//営業時間が日付を跨ぐ場合 openingHours < reservationTime > 0 
				isAfterMidnight && isBeforeOrAtLastReservation || isValidStartTime && closingTimeisAfterMidnight);

	}
}
