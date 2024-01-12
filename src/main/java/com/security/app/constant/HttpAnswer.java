package com.security.app.constant;

import com.security.app.dto.util.HttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class HttpAnswer {

    public static final String USER_SUCCESSFULLY_REGISTERED = "User successfully registered";
    public static final String WAREHOUSE_SUCCESSFULLY_REGISTERED = "Warehouse successfully registered";
    public static final String SUBJECT_SUCCESSFULLY_REGISTERED = "Subject successfully registered";
    public static final String MANAGER_SUCCESSFULLY_REGISTERED = "Manager successfully registered";
    public static final String MANAGER_SUCCESSFULLY_UNREGISTERED = "Manager successfully unregistered";
    public static final String APPLICATION_SUCCESSFULLY_SENT = "Application successfully sent";
    public static final String APPLICATION_SUCCESSFULLY_ACCEPTED = "Application successfully accepted";
    public static final String MOVIE_SUCCESSFULLY_REGISTERED = "Movie successfully registered";
    public static final String IMAGE_DELIVERED_SUCCESSFULLY = "Image delivered successfully";

    public static ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        HttpResponse body = new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(), message.toUpperCase());
        return new ResponseEntity<>(body, httpStatus);
    }

}
