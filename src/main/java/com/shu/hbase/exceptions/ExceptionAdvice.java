package com.shu.hbase.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.BindException;

@RestControllerAdvice()
public class ExceptionAdvice {

    private Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler
    @ResponseBody
    public Object exceptionHandler(Exception e) {
        String message = e.getMessage();

        switch (message) {
            case "0001":
                message = Exceptions.SERVER_CONNECTION_ERROR.getEmsg();
                break;

            case "0002":
                message = Exceptions.SERVER_PARAMSETTING_ERROR.getEmsg();
                break;

            case "0003":
                message = Exceptions.SERVER_AUTH_ERROR.getEmsg();
                break;

            case "0004":
                message = Exceptions.SERVER_UNAMEISNULL_ERROR.getEmsg();
                break;

            default:
                message = Exceptions.SERVER_OTHER_ERROR.getEmsg();
                break;

        }
        logger.error(message);
        return message;
    }

    @ExceptionHandler
    @ResponseBody
    public Object paramsExceptionHandler(BindException e) {
        logger.error(e.getMessage());
        return Exceptions.SERVER_PARAMSETTING_ERROR.getEmsg();
    }

    @ExceptionHandler
    @ResponseBody
    public Object typeExceptionHandler(HttpRequestMethodNotSupportedException e) {
        logger.error(e.getMessage());
        return Exceptions.SERVER_HTTPTYPE_ERROR.getEmsg();
    }
}
