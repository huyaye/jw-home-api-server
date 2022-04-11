package com.jw.home.exception;

public class BadRequestParamException extends RuntimeException {
    public static BadRequestParamException INSTANCE = new BadRequestParamException();
}
