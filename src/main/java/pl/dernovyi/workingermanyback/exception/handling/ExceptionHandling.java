package pl.dernovyi.workingermanyback.exception.handling;

import com.auth0.jwt.exceptions.TokenExpiredException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.dernovyi.workingermanyback.exception.*;
import pl.dernovyi.workingermanyback.model.dto.HttpResponse;

import javax.persistence.NoResultException;
import java.io.IOException;
import java.util.Objects;

@RestControllerAdvice
public class ExceptionHandling  implements ErrorController{
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private static final String ACCOUNT_LOCKED = "Ваш аккаунт был заблокирован! Обратитесь к администрации";
    private static final String METHOD_IS_NOT_ALLOWED = "This request method is not allowed on this endpoint. Please send a '%s' request";
    private static final String INTERNAL_SERVER_ERROR_MSG = "Произошла ошибка при обработке запроса";
    private static final String INCORRECT_CREDENTIALS ="Email / пароль не верны! Попробуйте еще раз.";
    private static final String ACCOUNT_DISABLED = "Ваш аккаунт отлючен. Обратитесь к администрации";
    private static final String ERROR_PROCESSING_FILE = "Error occurred while processing file";
    private static final String NOT_ENOUGH_PERMISSION = "Не достаточно прав";
    public static final String ERROR_PATH = "/error";

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<HttpResponse> accountDisabledException(){
        return createHttpResponse(HttpStatus.BAD_REQUEST, ACCOUNT_DISABLED);
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HttpResponse> badCredentialsException(){
        return createHttpResponse(HttpStatus.BAD_REQUEST, INCORRECT_CREDENTIALS);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public  ResponseEntity<HttpResponse> accessDeniedException(){
        return createHttpResponse(HttpStatus.FORBIDDEN, NOT_ENOUGH_PERMISSION);
    }

    @ExceptionHandler(LockedException.class)
    public  ResponseEntity<HttpResponse> lockedException(){
        return createHttpResponse(HttpStatus.UNAUTHORIZED, ACCOUNT_LOCKED);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public  ResponseEntity<HttpResponse> tokenExpiredException(TokenExpiredException exception){
        return createHttpResponse(HttpStatus.UNAUTHORIZED, exception.getMessage() );
    }

    @ExceptionHandler(EmailExistException.class)
    public  ResponseEntity<HttpResponse> emailExistException(EmailExistException exception){
        return createHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage() );
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public  ResponseEntity<HttpResponse> emailNotFoundException(EmailNotFoundException exception){
        return createHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage() );
    }

    @ExceptionHandler(PasswordNotCorrectException.class)
    public  ResponseEntity<HttpResponse> passwordNotCorrectException(PasswordNotCorrectException exception){
        return createHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage() );

    }

    @ExceptionHandler(UserNotFoundException.class)
    public  ResponseEntity<HttpResponse> userNotFoundException(UserNotFoundException exception){
        return createHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage() );
    }

    @ExceptionHandler(Exception.class)
    public  ResponseEntity<HttpResponse> internalServerErrorException(Exception exception){
        LOGGER.error(exception.getMessage());
        return createHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG );
    }

    @ExceptionHandler(NotAnImageFileException.class)
    public  ResponseEntity<HttpResponse> notAnImageFileException(NotAnImageFileException exception){
        LOGGER.error(exception.getMessage());
        return createHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage() );
    }

    @ExceptionHandler(ImageNotFoundException.class)
    public  ResponseEntity<HttpResponse> imageNotFoundException(ImageNotFoundException exception){
        return createHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage() );
    }

    @ExceptionHandler(ForbiddenFileException.class)
    public  ResponseEntity<HttpResponse> forbiddenFileException(ForbiddenFileException exception){
        LOGGER.error(exception.getMessage());
        return createHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage() );
    }

    @ExceptionHandler(EmptyFileException.class)
    public  ResponseEntity<HttpResponse> emptyFileException(EmptyFileException exception){
        return createHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage() );
    }

    @ExceptionHandler(CompressBytesException.class)
    public  ResponseEntity<HttpResponse> notAnImageFileException(CompressBytesException exception){
        return createHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage() );
    }

    @ExceptionHandler(NoResultException.class)
    public  ResponseEntity<HttpResponse> notFoundException(NoResultException exception){
        LOGGER.error(exception.getMessage());
        return createHttpResponse(HttpStatus.NOT_FOUND, exception.getMessage() );
    }

    @ExceptionHandler(IOException.class)
    public  ResponseEntity<HttpResponse> iOException(IOException exception){
        LOGGER.error(exception.getMessage());
        return createHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_PROCESSING_FILE );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public  ResponseEntity<HttpResponse> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exception){
        HttpMethod supportedMethod = Objects.requireNonNull(exception.getSupportedHttpMethods()).iterator().next();
        return createHttpResponse(HttpStatus.METHOD_NOT_ALLOWED, String.format(METHOD_IS_NOT_ALLOWED, supportedMethod));
    }

//    метод создания response
    private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus, String message){
        HttpResponse httpResponse = new HttpResponse(httpStatus.value(),httpStatus, httpStatus.getReasonPhrase().toUpperCase(), message );
        return new ResponseEntity<>(httpResponse,httpStatus );
    }

    @RequestMapping(ERROR_PATH)
    public  ResponseEntity<HttpResponse> notFound404(){
        return createHttpResponse(HttpStatus.NOT_FOUND, "Такого URL не существует" );
    }

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }
}
