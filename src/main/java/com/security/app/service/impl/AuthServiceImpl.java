package com.security.app.service.impl;

import com.security.app.dto.LoginUserDTO;
import com.security.app.exception.model.PasswordException;
import com.security.app.exception.model.UsernameExistException;
import com.security.app.model.User;
import com.security.app.model.UserPrincipal;
import com.security.app.repository.UserRepository;
import com.security.app.service.AuthService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import static com.security.app.constant.UserImplConstant.*;
import static com.security.app.enumeration.Role.ROLE_ADMIN;
import static com.security.app.enumeration.Role.ROLE_USER;

@Service
@Qualifier("userDetailsService")
@AllArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService, UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + username));
        UserPrincipal userPrincipal = new UserPrincipal(user);
        log.info(FOUND_USER_BY_USERNAME + username);
        return userPrincipal;
    }

    @Override
    public void registration(LoginUserDTO loginUserDTO) throws UsernameExistException, PasswordException {
        validateNewUsernameAndPassword(loginUserDTO);
        User user = new User();
        user.setUsername(loginUserDTO.getUsername());
        user.setPassword(encodePassword(loginUserDTO.getPassword()));
        if (userRepository.findAll().isEmpty()) {
            user.setRole(ROLE_ADMIN.name());
            user.setAuthorities(ROLE_ADMIN.getAuthorities());
        } else {
            user.setRole(ROLE_USER.name());
            user.setAuthorities(ROLE_USER.getAuthorities());
        }
        userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) throws UsernameExistException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameExistException(USERNAME_ALREADY_EXISTS));
    }

    private void validateNewUsernameAndPassword(LoginUserDTO loginUserDTO) throws UsernameExistException, PasswordException {
        if (userRepository.findByUsername(loginUserDTO.getUsername()).isPresent()){
            throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
        }
        if (!loginUserDTO.getPassword().equals(loginUserDTO.getPassword2())){
            throw new PasswordException(PASSWORD_IS_NOT_VALID);
        }
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }



}
