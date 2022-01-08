package com.jw.home.exception;

public class InvalidHomeException extends CustomBusinessException {
	private static final long serialVersionUID = 5577559296084970504L;

	public static InvalidHomeException INSTANCE = new InvalidHomeException();

	InvalidHomeException() {
		super();
		this.errorCode = 301;
		this.errorMessage = "invalid home";
	}
}
