package com.localweb.wagecalculatorapp.payload.DTO;

import lombok.Data;

@Data
public class ResponseByTotals {
    private UserDTO user;
    int hoursIn;
    int hoursOut;
    int totalHours;
    double totalAmount;
    
    public ResponseByTotals(){
        this.user = new UserDTO();
        this.hoursIn = 0;
        this.hoursOut = 0;
        this.totalHours = 0;
        this.totalAmount = 0;
    }
    
}
