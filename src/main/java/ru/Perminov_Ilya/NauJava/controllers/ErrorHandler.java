package ru.Perminov_Ilya.NauJava.controllers;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.InvalidParameterException;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String ResourceNotFoundExceptionHandler(ResourceNotFoundException ex) {
        return "Oh no, you misclick somewhere! Look at this: " + ex.getMessage();
    }

    @ExceptionHandler(InvalidParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String InvalidParameterExceptionHandler(InvalidParameterException ex) {
        return "Oh no, you entered an invalid parameter! Look at this: " + ex.getMessage();
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String MissingServletRequestParameterExceptionHandler(MissingServletRequestParameterException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String ExceptionHandler(Exception ex) {
        return "Oh, it looks like our server is broken. Please try again later. " + ex.getMessage();
    }
}
