package com.security.app.service;

import com.security.app.dto.LoginUserDTO;
import com.security.app.exception.model.PasswordException;
import com.security.app.exception.model.UsernameExistException;
import com.security.app.model.User;

public interface AuthService {

    User findByUsername(String username) throws UsernameExistException;
    void registration(LoginUserDTO loginUserDTO) throws UsernameExistException, PasswordException;

}
