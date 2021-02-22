package pl.dernovyi.workingermanyback.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;

import java.util.Date;

public class HttpResponse {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss", timezone = "Europa")
    private Date tameStamp;
    private int httpStatusCode;
//    статус в цифрах
    private HttpStatus httpStatus;
//    статус строка "OK"
    private String reason;
    private String message;

    public HttpResponse() {
    }

    public HttpResponse( int httpStatusCode, HttpStatus httpStatus, String reason, String message) {
        this.tameStamp = new Date();
        this.httpStatusCode = httpStatusCode;
        this.httpStatus = httpStatus;
        this.reason = reason;
        this.message = message;
    }

    public Date getTameStamp() {
        return tameStamp;
    }

    public void setTameStamp(Date tameStamp) {
        this.tameStamp = tameStamp;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
