package com.localweb.wagecalculatorapp.payload.DTO;

import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class ResponseByDayDTO {
    private LocalDate date;
    private List<DailyUserDTO> users = new ArrayList<>();
    private double total;
}
