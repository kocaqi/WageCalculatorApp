package com.localweb.wagecalculatorapp.payload.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ResponseByWorkingDayDTO {
    private LocalDate date;
    private UserDTO user;
    private int hours;
    private double amount;
}
