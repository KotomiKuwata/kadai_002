package com.example.kadai_002.form;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReservationInputForm {
	@NotNull(message = "来店日を入力してください。")
	private LocalDate reservationDate;

	@NotNull(message = "来店時間を入力してください。")
	private LocalTime reservationTime;

	@Min(value = 1, message = "来店人数は1人以上で入力してください。")
	private Integer numberOfPeople;

	// LocalDateTimeに変換するメソッド
	public LocalDateTime getReservationDateTime() {
		if (reservationDate != null && reservationTime != null) {
			return LocalDateTime.of(reservationDate, reservationTime);
		}
		return null;
	}
}