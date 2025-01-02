package com.example.kadai_002.form;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;

@Data

public class ReservationRegisterForm {
	
	private Integer storeId;
	
	private Integer userId;
	
	private LocalTime reservationTime;
	
	private LocalDate reservationDate;
	
	private Integer numberOfPeople;
	
}
