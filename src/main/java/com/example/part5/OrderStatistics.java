package com.example.part5;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class OrderStatistics {

    private String amount;

    private LocalDate date;

    @Builder
    private OrderStatistics(String amount, LocalDate date){
        this.amount = amount;
        this.date = date;

    }

}
