package com.la_casa_del_rosariello.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidGuestNumberException extends RuntimeException{
    public InvalidGuestNumberException(String message) {
        super(message);
    }
}
