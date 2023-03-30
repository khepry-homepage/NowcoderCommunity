package com.nowcoder.community.utils;

public class Constants {
    public static final int ACTIVATION_SUCCESS = 0;
    public static final int ACTIVATION_ERROR = 1;
    public static final int ACTIVATION_REPEAT = 2;
    public static final int TICKET_DEFAULT_DURATION = 3600 * 12;            //  单位: s
    public static final int TICKET_LONGER_DURATION = 3600 * 24 * 100;       //  单位: s
    public static final int RESET_PASSWORD_CAPTCHA_DURATION = 300;          //  单位: s
    public static final String LOGIN_TICKET = "login_ticket";               //  登录凭证cookie名
    public static final String LOGIN_USER = "login_user";                   //  登录的用户信息
    public static final String KAPTCHA_SESSION_KEY = "kaptcha_session_key";
    public static final String RESET_PASSWORD_KAPTCHA_SESSION_KEY = "reset_password_kaptcha_session_key";
}
