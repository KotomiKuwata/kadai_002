package com.example.kadai_002.form;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class ReviewRegisterForm {

	private Integer storeId;
	
	private Integer userId;
	
    private Integer rating;

    private String comment;
}
