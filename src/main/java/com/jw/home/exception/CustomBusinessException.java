package com.jw.home.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomBusinessException extends RuntimeException {
   protected Integer errorCode;

   protected String errorMessage;
}
