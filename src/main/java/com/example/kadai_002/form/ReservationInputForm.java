package com.example.kadai_002.form;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReservationInputForm {
    @NotNull(message = "来店日時を入力してください。")
    private LocalDateTime reservationDatetime;

    @Min(value = 1, message = "来店人数は1人以上で入力してください。")
    private Integer numberOfPeople;
}
