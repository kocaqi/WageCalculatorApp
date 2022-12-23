package com.localweb.wagecalculatorapp.payload.DTO;

import lombok.Data;

import java.time.Month;

@Data
public class MonthDTO {
    private Month month;
    private int year;
    private int hoursIn;
    private int hoursOut;
    private int totalHours;
    private double totalAmount;
}
