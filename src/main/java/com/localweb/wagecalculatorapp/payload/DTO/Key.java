package com.localweb.wagecalculatorapp.payload.DTO;

import lombok.Data;

import java.time.Month;

@Data
public class Key {
    private long userID;
    private Month month;
    private int year;

    public Key(long userId, Month month, int year) {
        this.userID = userId;
        this.month = month;
        this.year = year;
    }
}
