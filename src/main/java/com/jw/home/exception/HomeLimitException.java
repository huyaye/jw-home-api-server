package com.jw.home.exception;

public class HomeLimitException extends CustomBusinessException {
	private static final long serialVersionUID = -2730614668891610098L;

	public static HomeLimitException INSTANCE = new HomeLimitException();

	HomeLimitException() {
		super();
		this.errorCode = 302;
		this.errorMessage = "home limit";
	}
}
