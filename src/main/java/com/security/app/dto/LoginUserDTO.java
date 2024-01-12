package com.security.app.dto;

import lombok.Data;

@Data
public class LoginUserDTO {

    private String username;
    private String password;
    private String password2;

}
