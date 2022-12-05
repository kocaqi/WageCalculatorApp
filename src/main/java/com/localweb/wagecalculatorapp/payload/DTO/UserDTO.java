package com.localweb.wagecalculatorapp.payload.DTO;

import lombok.Data;

@Data
public class UserDTO {
    private long id;
    private String firstName;
    private String lastName;
    private String email;
}
