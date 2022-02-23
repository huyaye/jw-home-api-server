package com.jw.home.exception;

public class InvalidDeviceSpecException extends CustomBusinessException {
    public static InvalidDeviceSpecException INSTANCE = new InvalidDeviceSpecException();

    InvalidDeviceSpecException() {
        super();
        this.errorCode = 402;
        this.errorMessage = "invalid device spec";
    }
}
