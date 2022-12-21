package com.localweb.wagecalculatorapp.payload.DTO;

import lombok.Data;

@Data
public class DailyUserDTO {
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private double dailyWage;
}
