package com.security.app.constant;

public class SecurityConstant {
    public static final long EXPIRATION_TIME = 864_000_000; // 10 DAYS
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String JWT_TOKEN_HEADER = "Jwt-Token";
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token Cannot be verified";
    public static final String BUG_LLC = "Bug, TS";
    public static final String BUG_ADMINISTRATION = "Phone repair service";
    public static final String AUTHORITIES = "authorities";
    public static final String FORBIDDEN_MESSAGE = "You need to log in to access this page";
    public static final String ACCESS_DENIED_MESSAGE = "You do not have permission to access this page";
    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
}
