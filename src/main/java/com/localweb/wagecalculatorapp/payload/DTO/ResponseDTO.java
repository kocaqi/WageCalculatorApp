package com.localweb.wagecalculatorapp.payload.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ResponseDTO {
    private UserDTO userDTO;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private double amount;
}
