package com.localweb.wagecalculatorapp.payload.DTO;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

@Data
public class RequestDTO {
    @NotEmpty(message = "This Field cannot be null!")
    private String keyword;
    @NotEmpty(message = "This Field cannot be null!")
    private LocalDate dateFrom;
    @NotEmpty(message = "This Field cannot be null!")
    private LocalDate dateTo;
}
