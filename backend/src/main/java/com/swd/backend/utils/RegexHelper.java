package com.swd.backend.utils;

public class RegexHelper {
    public static final String EMAIL_REGEX = "^[_A-Za-z\\d-+]+(\\.[_A-Za-z\\d-]+)*@[A-Za-z\\d-]+(\\.[A-Za-z\\d]+)*(\\.[A-Za-z]{2,})$";
    public static final String OTP_REGEX = "\\d{6}";
    public static final String PHONE_REGEX_LOCAL = "^[0][0-9]{9}$";
}
