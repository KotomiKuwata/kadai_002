package com.example.kadai_002.form;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReservationRegisterForm {
	
	private Integer storeId;
	
	private Integer userId;
	
	private LocalDateTime reservationDatetime;
	
	private Integer numberOfPeople;
	
}
