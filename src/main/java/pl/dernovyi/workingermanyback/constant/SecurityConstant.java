package pl.dernovyi.workingermanyback.constant;

public class SecurityConstant {
    public static final long EXPIRATION_TIME = 432_000_000; //5 days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String JWT_TOKEN_HEADER  = "Jwt-Token";
    public static final String TOKEN_CANNOT_BE_VERIFIED  = "Токен не может быть прочитан";
    public static final String WORK_IN_GERMANY  = "Work In Germany";
    public static final String ADMINISTRATION  = "Work In Germany administration";
    public static final String AUTHORITIES  = "Authorities";
    public static final String FORBIDDEN_MESSAGE  = "Нужно зарегестрироваться для доступа к странице";
    public static final String ACCESS_DENIED_MESSAGE  = "У Вас нет доступа к этой странице";
    public static final String OPTIONS_HTTP_METHOD  = "OPTIONS";
    public static final String[] PUBLIC_URLS = {"/register","/auth/login", "/auth/register", "/user/resetPassword/*"};
//    public static final String[] PUBLIC_URLS = {"**"};
//            "/images/**"
}
