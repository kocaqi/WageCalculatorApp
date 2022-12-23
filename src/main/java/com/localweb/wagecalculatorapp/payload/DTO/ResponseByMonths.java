package com.localweb.wagecalculatorapp.payload.DTO;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ResponseByMonths {

    private UserDTO user;
    private int hoursIn;
    private int hoursOut;
    private int totalHours;
    private double totalAmount;
    private List<MonthDTO> months = new ArrayList<>();

}