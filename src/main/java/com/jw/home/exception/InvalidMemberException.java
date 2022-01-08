package com.jw.home.exception;

public class InvalidMemberException extends CustomBusinessException {
	private static final long serialVersionUID = -7080774638171358036L;

	public static InvalidMemberException INSTANCE = new InvalidMemberException();

	InvalidMemberException() {
		super();
		this.errorCode = 101;
		this.errorMessage = "invalid member";
	}
}
