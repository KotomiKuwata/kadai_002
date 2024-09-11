package com.example.kadai_002.form;

import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StoreRegisterForm {
	@NotBlank(message = "店舗名を入力してください。")
	private String name;
	
	private MultipartFile imageFile;
	
	@NotBlank(message = "説明を入力してください。")
	private String description;
	
	@NotNull(message = "開店時間を入力してください。")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime openingHours;

    @NotNull(message = "閉店時間を入力してください。")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime closingTime;
	
	@NotBlank(message = "郵便番号を入力してください。")
    private String postalCode;
	
	 @NotBlank(message = "住所を入力してください。")
     private String address;
	 
	 @NotBlank(message = "電話番号を入力してください。")
     private String phoneNumber;
	 
	 @NotBlank(message = "定休日を入力してください。")
     private String closedDay;
	 
	 @NotNull(message = "カテゴリを入力してください。")
	 private Integer categoryId;
}