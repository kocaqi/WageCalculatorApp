package com.localweb.wagecalculatorapp.payload.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RequestDTO {
    private String keyword;
    private LocalDate dateFrom;
    private LocalDate dateTo;
}
