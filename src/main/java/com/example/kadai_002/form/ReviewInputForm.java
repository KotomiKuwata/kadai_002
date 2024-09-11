package com.example.kadai_002.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReviewInputForm {

	@NotNull(message = "星の数１〜５で評価してください。")
    @Min(value = 1, message = "評価は1以上にしてください。")
    @Max(value = 5, message = "評価は5以下にしてください。")
    private Integer rating;

    @NotBlank(message = "コメントを入力してください。")
    @Size(max = 200, message = "コメントは200文字以内で入力してください。")
    private String comment;

    private Integer storeId;
    private Integer userId;
}
